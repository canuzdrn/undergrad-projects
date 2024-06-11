# MPI Programming Project (CmpE 300 - Analysis Of Algorithms)

You can execute the code via following command:
### `mpiexec -n 5 python main.py --input_file data/sample_text.txt --merge_method MASTER --test_file data/test.txt`

Where;
- -n represents the number of worker(s) including the master (5 for the case above)
- --input_file is the flag for the name (location) of the text file to be processed
- --merge_method is basically a flag for the merge method among workers and master, which is MASTER for the above case (check the description for further details)
- --test_file is the flag for name (location) of the bigrams that we will be dealing

### IMPORTANT NOTE: 
Since the number of processes can exceed the number of cores in computer, Open MPI may throw an error. The reason of that error is that the Open MPI basically wants to know beforehand if the number of processes will exceed the number of cores in the system. In order not to encounter with such an error you need to run the program with the --oversubscribe command option. (Number of processes and the file names are given arbitrary)
### `mpiexec --oversubscribe -n 13 python main.py --input_file data/sample_text.txt --merge_method MASTER --test_file data/test.txt`
