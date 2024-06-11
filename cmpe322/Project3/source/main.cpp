#include <iostream>
#include <fstream>
#include <pthread.h>
#include <string.h>
#include <unistd.h>
#include <vector>
#include <sstream>
#include <time.h>

using namespace std;

/* Makefile will create an executable named "simulation" */

class customerInfo {    /* customer class (basically a data class that only holds the customer's info) */
    public:
        int sleep_time;
        int machine_num;
        string company;
        int amount;
        string name;
};

ofstream out;   /* output file */

/* If the ticket vending machine that is picked by the customer is currently serving to another
customer, the incoming customer should wait until it becomes the head of the queue (mutex lock usage from description) */
pthread_mutex_t write_locks[10]; /* mutex lock array for info transfer from customers to vending machines */

/* there is a single bank account for each company so,the simultaneous prepayments made through different ticket vending 
machine instances should not lead to an inconsistent situation. We need to ensure that more than one vending machine 
cannot modify the bank balance simultaneously (mutex lock usage from description) */
pthread_mutex_t update_locks[5];    /* mutex lock array for vending machines to write information to the companies' total balance */

pthread_mutex_t print_lock;         /* mutex lock for output, ensure only one thread can write to the ouput file */

int balances[5];            /* stores the total balance for each company */   

vector<customerInfo> payment_infos;         /* stores the customer's info at any given time (one for each vending machine) */
int busy[10];                               /* stores a bit for each vending machines -- 1 -> if v.m is busy, 0 -> if vm is waiting for a customer */
int done[10];                               /* stores a bit for each vending machines -- 1 -> if v.m is done with the current customer, 0 -> if still processing */

vector<customerInfo> customer_infos;      /* holds customer infos */

int n;                              /* number of customers */

int processed = 0;                  /* number of processed customers */

void* machine(void* param){
    int machine_num = *((int*) param);
    while (true)
    {
        if(busy[machine_num-1] == 1){
            customerInfo payment_info = payment_infos[machine_num - 1];

            string company_name = payment_info.company;
            int amount = payment_info.amount;
            string customer_name = payment_info.name;

            int update_lock_index;
            if (company_name == "Kevin")
            {
                update_lock_index = 0;
            }
            else if (company_name == "Bob")
            {
                update_lock_index = 1;
            }
            else if (company_name == "Stuart")
            {
                update_lock_index = 2;
            }
            else if (company_name == "Otto")
            {
                update_lock_index = 3;
            }
            else
            {
                update_lock_index = 4;
            }

            pthread_mutex_lock(&update_locks[update_lock_index]);

            balances[update_lock_index] += amount;

            pthread_mutex_unlock(&update_locks[update_lock_index]);

            pthread_mutex_lock(&print_lock);

            processed++;

            out << "[vtm"  << machine_num << "]: " << customer_name << "," << amount << "TL," << company_name << endl;

            pthread_mutex_unlock(&print_lock);

            done[machine_num-1] = 1;
            busy[machine_num-1] = 0;
        }

        if (processed >= n)
        {
            break;
        }
    }

    pthread_exit(NULL);         /* return statement makes pthreads exit */
}

void* customer(void* param){
    customerInfo info = *((customerInfo*)param);    /* corresponding customer info has given as a parameter to the starting routine */

    int sleep_time = info.sleep_time;   /* in milliseconds */
    int machine_num = info.machine_num;
    
    timespec ts;
    ts.tv_sec = sleep_time / 1000;
    ts.tv_nsec = (sleep_time % 1000) * 1000000;
    nanosleep(&ts,NULL);

    pthread_mutex_lock(&write_locks[machine_num-1]);

    payment_infos[machine_num-1] = info;
    done[machine_num-1] = 0;
    busy[machine_num-1] = 1;
    
    while(true){
        if(done[machine_num-1] == 1){
            break;
        }
    }
    pthread_mutex_unlock(&write_locks[machine_num - 1]);

    pthread_exit(NULL);        /* return statement makes pthreads exit */
}
 
int main(int argc, char* argv[]){

    if(argc != 2){
        cout << "Invalid number of argument" << endl;
    }
    
    string file_name = argv[1];
    ifstream input_file;
    input_file.open(file_name);

    string raw_filename = file_name.substr(0, file_name.find_last_of("."));
    raw_filename += "_log.txt";
    out.open(raw_filename);
    if(!out.is_open()){
        cout << "An error has occured while openning the output file" << endl;
        return 0;
    }

    string number_of_customer;
    getline(input_file,number_of_customer);
    n = stoi(number_of_customer);
    
    payment_infos.resize(10);
    customer_infos.resize(n);
    string customer_info;

    string company_names[5] = {"Kevin","Bob","Stuart","Otto","Dave"};

    int index = 0;
    while (getline(input_file, customer_info)){
        string name = ",Customer" + to_string(index + 1); /* store customer info with their names since termination order may differ */
        customer_info += name;

        stringstream c_info(customer_info); /* holds <sleep time, vending machine num, company name, payment amount, customer name> respectively */

        int sleep_time;
        int machine_num;
        string company_name;
        int payment_amount;
        string customer_name;

        string temp;
        
        getline(c_info,temp,',');
        sleep_time = stoi(temp);
        getline(c_info,temp,',');
        machine_num = stoi(temp);
        getline(c_info,temp,',');
        company_name = temp;
        getline(c_info,temp,',');
        payment_amount = stoi(temp);
        getline(c_info,temp,',');
        customer_name = temp;

        customerInfo cust_info;
        cust_info.sleep_time = sleep_time;
        cust_info.machine_num = machine_num;
        cust_info.company = company_name;
        cust_info.amount = payment_amount;
        cust_info.name = customer_name;
        customer_infos[index] = cust_info;

        index++;
    }

    input_file.close();

     /* initializing mutex locks (need to be done before thread creation since mutexes are used inside threads) */
    for(int i=0;i<10;i++){
        pthread_mutex_init(&write_locks[i],NULL);
    }
    for(int i=0;i<5;i++){
        pthread_mutex_init(&update_locks[i],NULL);
    }

    pthread_mutex_init(&print_lock,NULL);

    /* thread creation section */
    pthread_t vending_machines[10];
    int vending_machine_nums[10];
    for(int i=0;i<10;i++){
        vending_machine_nums[i] = i+1;
    }

    for(int i=0;i<10;i++){
        pthread_create(&vending_machines[i],NULL,machine,&vending_machine_nums[i]); /* gave machine number as a parameter to starting routine -> will use this number as an index for mutex lock array(s)*/
    }

    pthread_t customers[n];
    for(int i=0;i<n;i++){
        pthread_create(&customers[i],NULL,customer,&customer_infos[i]); /* gave customer info as a parameter to starting routine -> will use these infos to obtain machine number, sleep time,...*/
    }

    /* thread joining section */
    for(int i=0;i<10;i++){
        if(pthread_join(vending_machines[i],NULL) != 0){
            cout << "Error occured with thread termination, Thread : vending machine no: " << i+1 << endl;
            return -1;
        }
    }

    for(int i=0;i<n;i++){
        if(pthread_join(customers[i],NULL) != 0){
            cout << "Error occured with thread termination, Thread : customer no: " << i+1 << endl;
            return -2;
        }
    }


    out << "[Main]: All payments are completed" << endl;

    for(int i=0;i<5;i++){
        out << "[Main]: " << company_names[i] << ": " << balances[i] << endl;
    }
    out.close();
    
    /* destroying mutexes in order to free up the storage */
    for(int i=0;i<10;i++){
        pthread_mutex_destroy(&write_locks[i]);
    }

    for(int i=0;i<5;i++){
        pthread_mutex_destroy(&update_locks[i]);
    }

    pthread_mutex_destroy(&print_lock);
    return 0;
}