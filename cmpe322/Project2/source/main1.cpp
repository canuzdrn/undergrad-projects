#include <iostream>
#include <fstream>
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

void findMin(){
    int result = mylist[0];
    for(int i=1;i<N;i++){
        if(mylist[i] < result){
            result = mylist[i];
        }
    }
    min_elem = result;
}

void findMax(){
    int result = mylist[0];
    for(int i=1;i<N;i++){
        if(mylist[i] > result){
            result = mylist[i];
        }
    }
    max_elem = result;
}

void findRange(){
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
    
}

void findMode(){
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

}

void findMedian(){
    sort(mylist.begin(),mylist.end());
    float result;

    if(N%2 == 0){
        result = (mylist[(N/2)-1] + mylist[N/2]) / 2.0;
    }
    else{
        result = mylist[N/2] / 1.0;
    }
    median = result;
}

void findSum(){
    long int result = 0;
    for(int i=0;i<N;i++){
        result += mylist[i];
    }
    sum = result;
}

void findArithmeticMean(){
    long int sum = 0;
    float result;
    for(int i=0;i<N;i++){
        sum += mylist[i];
    }
    result = (float) sum / N;
    
    arithmetic_mean = result;
}

void findHarmonicMean(){
    float denominator = 0;
    float result;
    for(int i=0;i<N;i++){
        denominator += (float) 1/mylist[i];
    }
    result = (float) N / denominator;
    
    harmonic_mean = result;
}

void findSD(){
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
}

void findIR(){       /* designed the array such that upper_part to lower_part (upper_part starts with index 0) */
    sort(mylist.begin(),mylist.end());
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

    if(argc != 2){
        cout << "Please give argument" << endl;
        return -1;
    }

    N = atoi(argv[1]);
    mylist.resize(N);

    for(int i=0;i<N;i++){
        /* int random = rand() % 9001 + 1000;
        cout << random << endl; */
        mylist[i] = rand() % 9001 + 1000;
   
    }

    findMin();
    findMax();
    findRange();
    findMode();
    findMedian();
    findSum();
    findArithmeticMean();
    findHarmonicMean();
    findSD();
    findIR();

    auto end = chrono::steady_clock::now();

    ofstream out("output1.txt");

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