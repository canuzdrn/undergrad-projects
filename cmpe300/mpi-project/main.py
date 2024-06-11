##Student Name: Necdet Can Uzduran
##Student Number: 2019400195
##Compile Status: Compiling
##Program Status: Working
##Notes: Comments are added into code file in order to ease the understanding of workflow

import sys
from mpi4py import MPI

####################################### Initial Global Variable Declarations
bigram_dict = {}               # Dictionary to store the bigrams of each parallel process.
unigram_dict = {}              # Dictionary to store the unigrams of each parallel process.
comm = MPI.COMM_WORLD          # Initializing an instance of the default communicator.
world_size = comm.Get_size()   # The number of parallel process including the master.
rank = comm.Get_rank()         # The special ID for each parallel process.

def MASTER(fileLines):  
	for line in fileLines:                         # Loop over each line in the list sent by the master.
		tokenList = line.split()                   # Tokenize the line using split.
		tokensize = len(tokenList)                 # Size of the tokens.
		for counter in range(tokensize - 1):       # loop from 0 to tokensize - 2 to avoid an overflow while acquiring the bigrams
			bigram = tokenList[counter] + " " + tokenList[counter + 1]                     # forming a bigram by concatenating 2 unigrams.
			
			if tokenList[counter] in unigram_dict.keys():                                  # If the unigram is in the dictionary, increment 1. Otherwise, add the new unigram to the dictionary and assign its value to 1.
				unigram_dict[tokenList[counter]] = unigram_dict[tokenList[counter]] + 1
			else:
				unigram_dict[tokenList[counter]] = 1
			                                  
			if bigram in bigram_dict.keys():                                               # If the bigram is in the dictionary, increment 1. Otherwise, add the new bigram  to the dictionary and assign its value to 1.
				bigram_dict[bigram] = bigram_dict[bigram] + 1
			else:
				bigram_dict[bigram] = 1
		if tokenList[tokensize - 1] in unigram_dict.keys():                                # Dont forget to include the last token, </s>, in the unigram dictionary.
			unigram_dict[tokenList[tokensize - 1]] = unigram_dict[tokenList[tokensize - 1]] + 1
		else:
			unigram_dict[tokenList[tokensize - 1]] = 1		
	comm.send(unigram_dict,dest = 0,tag = rank )                                           # Send the unigram dictionary back to the master. The rank is assigned as a tag to the message.
	comm.send(bigram_dict, dest = 0,tag = rank + world_size)		               # Send the bigram dictionary back to the master. A different tag is assigned to distinguish the second message from the first one.


def WORKERS(fileLines):
	# dicts calculated by workers itself only --> will be merged with the coming dicts later
	workers_unigram_dict = {}
	workers_bigram_dict = {}

	for line in fileLines:                         # Loop over each line in the list sent by the master.
		tokenList = line.split()                   # Tokenize the line using split.
		tokensize = len(tokenList)                 # Size of the tokens.
		for counter in range(tokensize - 1):       # loop from 0 to tokensize - 2 to avoid an overflow while acquiring the bigrams
			bigram = tokenList[counter] + " " + tokenList[counter + 1]                     # forming a bigram by concatenating 2 unigrams.
			
			if tokenList[counter] in workers_unigram_dict.keys():                                  # If the unigram is in the dictionary, increment 1. Otherwise, add the new unigram to the dictionary and assign its value to 1.
				workers_unigram_dict[tokenList[counter]] = workers_unigram_dict[tokenList[counter]] + 1
			else:
				workers_unigram_dict[tokenList[counter]] = 1
			                                  
			if bigram in workers_bigram_dict.keys():                                               # If the bigram is in the dictionary, increment 1. Otherwise, add the new bigram  to the dictionary and assign its value to 1.
				workers_bigram_dict[bigram] = workers_bigram_dict[bigram] + 1
			else:
				workers_bigram_dict[bigram] = 1
		if tokenList[tokensize - 1] in workers_unigram_dict.keys():                                # Dont forget to include the last token, </s>, in the unigram dictionary.
			workers_unigram_dict[tokenList[tokensize - 1]] = workers_unigram_dict[tokenList[tokensize - 1]] + 1
		else:
			workers_unigram_dict[tokenList[tokensize - 1]] = 1
	
	# RECEIVING DATA FROM PREVIOUS NODE
	if rank != 1:						# if the node is not the first worker, it can receive data from the previous worker hence we need to merge the coming data and the calculated data that node is calculated by itself	
		coming_unigram_dict = comm.recv(source = rank-1, tag = rank-1)
		coming_bigram_dict = comm.recv(source = rank-1, tag = rank-1 + world_size)

		for unigram_key in coming_unigram_dict.keys():
			if unigram_key in workers_unigram_dict.keys():
				workers_unigram_dict[unigram_key] = workers_unigram_dict[unigram_key] + coming_unigram_dict[unigram_key]
			else:
				workers_unigram_dict[unigram_key] = coming_unigram_dict[unigram_key]
		
		for bigram_key in coming_bigram_dict.keys():
			if bigram_key in workers_bigram_dict.keys():
				workers_bigram_dict[bigram_key] = workers_bigram_dict[bigram_key] + coming_bigram_dict[bigram_key]
			else:
				workers_bigram_dict[bigram_key] = coming_bigram_dict[bigram_key]

	# SENDING DATA TO NEXT NODE
	if rank != world_size-1:							# If not the last worker -> send the calculated data to the next worker
		comm.send(workers_unigram_dict,dest = rank+1,tag = rank )                                           
		comm.send(workers_bigram_dict, dest = rank+1,tag = rank + world_size)

	else:										# If the last worker send the data to the master
		comm.send(workers_unigram_dict,dest = 0,tag = rank )                    # instead of the next worker
		comm.send(workers_bigram_dict, dest = 0,tag = rank + world_size)

####################### The program's main driver part

if rank == 0:	# if node is the master
	inputfile = open(sys.argv[2], 'r')    # read the file through the path passed in by the third arguments.
	fileLines = inputfile.readlines()     # convert the lines in the file to a list of lines.
	lineCount = len(fileLines)            
	lineCounter = 0
	quotient = lineCount // (world_size - 1)   # We are trying to fairly distribute the lines.
	remainder = lineCount % (world_size - 1)   # While we have a remainder, a process will be assigned quotient + 1 lines and the remainder would be decremented to 0. After that, we will assign the remaining processes quotient-number of lines.
	for worker in range (1,world_size):        # This allocation algorithm is written to satisfy the FIRST REQUIREMENT
		if remainder > 0:
			comm.send(fileLines[int(lineCounter) : int(lineCounter + quotient + 1)],dest = int(worker))
			remainder = remainder - 1 
			lineCounter = lineCounter + quotient + 1
		else:
			comm.send(fileLines[int(lineCounter) : int(lineCounter + quotient)],dest = int(worker))
			lineCounter = lineCounter + quotient

else:  # if node is a worker        #Print the process' rank and number of lines, satisfying part of the second requirment
	fileLines = comm.recv(source = 0)
	print("The worker of rank:",rank,"received",len(fileLines),"lines from the input file")
	if sys.argv[4] == "MASTER":
		MASTER(fileLines)
	else:
		WORKERS(fileLines)

####################### Merging occurs below based on the 5th token
if rank == 0:
	if sys.argv[4] == "MASTER":                             			### Part of the SECOND REQUIREMENT
		for worker in range(1,world_size):
			temp_unigram_dict = comm.recv(source = worker ,tag = worker)
			temp_bigram_dict = comm.recv(source = worker,tag = world_size + worker)
			for key in temp_unigram_dict.keys():
				if key in unigram_dict.keys():
					unigram_dict[key] = unigram_dict[key] + temp_unigram_dict[key]
				else:
					unigram_dict[key] = temp_unigram_dict[key]

			for key in temp_bigram_dict.keys():
				if key in bigram_dict.keys():
					bigram_dict[key] = bigram_dict[key] + temp_bigram_dict[key]
				else:
					bigram_dict[key] = temp_bigram_dict[key]

	elif sys.argv[4] == "WORKERS":
		unigram_dict= comm.recv(source = world_size-1, tag = world_size-1)
		bigram_dict= comm.recv(source = world_size-1, tag = world_size-1 + world_size)

	testFile  = open(sys.argv[6], 'r')  ##Open Test file
	bigramNo  = 0       ##Get the total number of bigrams in the Master's bigram dictionary
	unigramNo = 0       ##Get the total number of unigrams in the Master's unigram dictionary
	for key in bigram_dict.keys():
		bigramNo += bigram_dict[key]
	for key in unigram_dict.keys():
		unigramNo += unigram_dict[key]	
	testFileLines = testFile.readlines()   ##Read each line in the test file
	for line in testFileLines:
		firstUnigram = line.split(" ")[0] 
		secondUnigram = line.split(" ")[1].strip("\n") 
		bigram = firstUnigram+" "+secondUnigram

		 ###Checking the validity of the division operation
		if (bigram_dict.get(bigram) == None or unigram_dict.get(firstUnigram) == None or unigramNo == 0 or bigramNo == 0): 
			conditionalProbability = 0                                             ###Return zero if the conditions are not satisfied
		else:                             
			conditionalProbability = (bigram_dict[bigram]) / (unigram_dict[firstUnigram])
		print("The bigram (",bigram,") has the corresponding conditional probability of",conditionalProbability) #Printing part
	testFile.close()   ###Files are closed at the end
	inputfile.close()  ###Requirement 4 is satisfied	

exit()
