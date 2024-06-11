#include <iostream>
#include <fstream>
#include <pthread.h>
#include <iterator>
#include <map>
#include <stdlib.h>
#include <cmath>
#include <time.h>
#include <bits/stdc++.h>
#include <chrono>
#include <vector>

using namespace std;


int N;
vector<int> mylist;

int min_elem;
int max_elem;
int range;
int mode;
float median;
long int sum;
float arithmetic_mean;
float harmonic_mean;
float standard_dev;
float interquart_range;

void* findMin(void* param){
    int result = mylist[0];
    for(int i=1;i<N;i++){
        if(mylist[i] < result){
            result = mylist[i];
        }
    }
    min_elem = result;
    pthread_exit(0);
}

void* findMax(void* param){
    int result = mylist[0];
    for(int i=1;i<N;i++){
        if(mylist[i] > result){
            result = mylist[i];
        }
    }
    max_elem = result;
    pthread_exit(0);
}

void* findRange(void* param){
    int localMin = mylist[0];
    int localMax = mylist[0];

    for(int i=1;i<N;i++){
        if(mylist[i] < localMin){
            localMin = mylist[i];
        }
        
        if(mylist[i] > localMax){
            localMax = mylist[i];
        }
    }
    range = localMax - localMin;
    pthread_exit(0);
}

void* findMode(void* param){
    map<int,int> m;
    int max_count = 0;
    int result = -1;

    for(int i=0;i<N;i++){
        if(m.count(mylist[i]) == 0){       
            m.insert({mylist[i],1});
        }
        else{
            int val = m.find(mylist[i]) -> second;
            val++;
            m.find(mylist[i]) -> second = val;
        }
    }

    map<int,int>::iterator it;

    for(it = m.begin(); it!=m.end();it++){
        int key = it -> first;
        int value = it -> second;
        if(value > max_count){
            max_count = value;
            result = key;
        }
    }

    mode = result;
    pthread_exit(0);
}

void* findMedian(void* param){
    float result;

    if(N%2 == 0){
        result = (mylist[(N/2)-1] + mylist[N/2]) / 2.0;
    }
    else{
        result = mylist[N/2] / 1.0;
    }
    median = result;
    pthread_exit(0);
}

void* findSum(void* param){
    long int result = 0;
    for(int i=0;i<N;i++){
        result += mylist[i];
    }
    sum = result;
    pthread_exit(0);
}

void* findArithmeticMean(void* param){
    long int sum = 0;
    float result;
    for(int i=0;i<N;i++){
        sum += mylist[i];
    }
    result = (float) sum / N;
    
    arithmetic_mean = result;
    
    pthread_exit(0);
}

void* findHarmonicMean(void* param){
    float denominator = 0;
    float result;
    for(int i=0;i<N;i++){
        denominator += (float) 1/mylist[i];
    }
    result = (float) N / denominator;
    
    harmonic_mean = result;
    
    pthread_exit(0);
}

void* findSD(void* param){
    long int sum = 0;
    float calculated_mean;
    for(int i=0;i<N;i++){
        sum += mylist[i];
    }
    calculated_mean = (float) sum / N;

    float nominator = 0;
    for(int i=0;i<N;i++){
        nominator += pow(mylist[i]-calculated_mean,2);
    }
    float result = sqrt((float) nominator / (N-1));
    
    standard_dev = result;
    
    pthread_exit(0);
}

void* findIR(void* param){       /* designed the array such that upper_part to lower_part (upper_part starts with index 0) */
    float result;
    int upper_length = N / 2;
    int lower_length = N / 2;
    float upper_mean,lower_mean;

    if(N%2 == 0){
        if(upper_length % 2 == 0) {
            upper_mean = (mylist[(upper_length/2)-1] + mylist[lower_length/2]) / 2.0;
            lower_mean = (mylist[upper_length + lower_length/2 - 1] + mylist[upper_length + lower_length/2]) / 2.0;
        }
        else {
            upper_mean = mylist[upper_length / 2];
            lower_mean = mylist[upper_length + lower_length / 2];
        }
    }
    else {
        if(upper_length % 2 == 0) {
            upper_mean = (mylist[(upper_length/2)-1] + mylist[upper_length/2]) / 2.0;
            lower_mean = (mylist[upper_length + lower_length/2] + mylist[upper_length + lower_length/2 + 1]) / 2.0;
        }
        else {
            upper_mean = mylist[upper_length / 2];
            lower_mean = mylist[upper_length + lower_length / 2 + 1];
        }
    }

    result = lower_mean - upper_mean;
    interquart_range = result;
    pthread_exit(0);
}

void printFloat(ofstream& out, float value){
    if(value == (int) value){
        out << (int) value << endl;
    }
    else{
        out << value << endl;
    }
}


int main(int argc, char* argv[]){

    auto start = chrono::steady_clock::now();

    if(argc != 3){
        cout << "Missing argument (N or number of threads)" << endl;
        return -1;
    }

    N = atoi(argv[1]);
    mylist.resize(N);

    for(int i=0;i<N;i++){
        mylist[i] = rand() % 9001 + 1000;
    }
    
    //
    sort(mylist.begin(),mylist.end()); /* sort the list in main array in order to prevent conflicts on thread operations*/
    //

    int remaining_tasks = 10;
    typedef void* (*tasktype) (void*);
    tasktype tasks[10];            /* we are dealing with 10 tasks*/
    tasks[0]= findMin;
    tasks[1]= findMax;
    tasks[2]= findRange;
    tasks[3]= findMode;
    tasks[4]= findMedian;
    tasks[5]= findSum;
    tasks[6]= findArithmeticMean;
    tasks[7]= findHarmonicMean;
    tasks[8]= findSD;
    tasks[9]= findIR;

    
    int num_threads = atoi(argv[2]);
    pthread_t threads[num_threads]; /* here our array of threads (length = given number of threads argument)*/

    while(remaining_tasks > 0){
        int creation_arr[num_threads];
        for(int i=0;i<num_threads;i++){
            creation_arr[i] = -1;
        }
        if(remaining_tasks >= num_threads){
            for(int i=0;i<num_threads;i++){
                int success = pthread_create(&threads[i],NULL,tasks[10-remaining_tasks],NULL); /* success = 0 -> if successfull*/
                creation_arr[i] = success;
                remaining_tasks--;
            }
        }
        else {
            for(int i=0;i<remaining_tasks;i++){
                int success = pthread_create(&threads[i],NULL,tasks[10-remaining_tasks],NULL);
                creation_arr[i] = success;
                remaining_tasks--;
            }
        }
        for(int i=0;i<num_threads;i++){
            if(creation_arr[i] == 0){
                pthread_join(threads[i],NULL);
            }
        }
    }

    auto end = chrono::steady_clock::now();

    ofstream out("output4.txt");

    out << setprecision(5) << fixed;
    out << min_elem << endl;
    out << max_elem << endl;
    out << range << endl;
    out << mode << endl;
    printFloat(out,median);
    out << sum << endl;
    printFloat(out,arithmetic_mean);
    printFloat(out,harmonic_mean);
    printFloat(out,standard_dev);
    printFloat(out,interquart_range);
    out << chrono::duration_cast<chrono::nanoseconds>(end - start).count() * 1e-9 << endl;

    out.close();
    return 0;
}