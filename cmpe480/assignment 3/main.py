inp = input()
splitted = inp.split("[")
prior_query = splitted[0].strip().split(" ")
post_query = splitted[1].split("]")
query = post_query[0].strip()

probabilities = list(map(float,prior_query[:5]))
algorithm_type = prior_query[5]
queries = query.split(" ")      # True = 0 and False = 1 in order to use indexing among lists/matrices
smoothing_k = 0
if algorithm_type == "S":
    smoothing_k = int(post_query[1].strip())

for j in range(len(queries)):
    if queries[j] == "T":
        queries[j] = 0
    else:
        queries[j] = 1

####
        
p0 = probabilities[0]
p1 = probabilities[1]
p2 = probabilities[2]
p3 = probabilities[3]
p4 = probabilities[4]

initial_dist = [p0,1-p0]                    # prob rain init, prob not rain init
transition_table = [[p1,1-p1],[p2,1-p2]]    # [rain to rain , rain to not rain], [not rain to rain , not rain to not rain]
emission_table = [[p3,1-p3],[p4,1-p4]]      # [rain to umbrella, rain to not umbrella], [not rain to umbrella, not rain to not umbrella]

####

def list_addition(list1, list2):
    if len(list1) != len(list2):
        print("Cannot add lists with two different lengths")
        return
    for i in range(len(list1)):
        list1[i] += list2[i]
    return list1

def list_multiplication(list1, list2):
    if len(list1) != len(list2):
        print("Cannot multiply lists with two different lengths")
        return
    for i in range(len(list1)):
        list1[i] *= list2[i]
    return list1

def mult_list_scalar(list1, scalar):
    return [scalar * j for j in list1]

def normalize(list1):
    summation = list1[0] + list1[1]
    coeff = 1.0 / summation
    return mult_list_scalar(list1,coeff)

def viterbi_path(p_dist):
    path = []
    for row in p_dist:
        if row[0] > row[1]:
            path.append("T")
        else:
            path.append("F")

    return path

def print_mle(path, p_dist):    # p_dist is matrix
    path_list = []
    for i in path:
        path_list.append(i)

    path_str = "[" + " ".join(path_list) + "]"
    
    dist_list = []
    for row in p_dist:
        #dist_list.append(f"<{row[0]}, {row[1]}>")
        dist_list.append(f"<{row[0]:.2f}, {row[1]:.2f}>")
    
    dist_str = "[" + ", ".join(dist_list) + "]"

    print(path_str + " " + dist_str)

    
####

def filtering(evidences):

    num_of_states = len(initial_dist)   # we have 2 states (rain, no rain) = length of given initial dist = len([prob0, 1 - prob0])      
    num_of_evidences = len(evidences)   # how many evidence we have observed/given
    
    # initialize forward prob table (each row will have two probabilities that corresponds to rain and not rain given evidence on that row)
    forward_probabilities = []  # forward probabilities will hold the probabilities we have calculated each step
    for i in range(num_of_evidences):
        forward_probabilities.append([0.0] * num_of_states)

    # base of forward probabilities table using the given first (0th) evidence of the evidence sequence (and also using the initial distributions <prob0>)
    current_evidence = 0
    for state in range(num_of_states):
        given_current_evidence = evidences[current_evidence]
        state_probability = 0
        for prior_state in range(num_of_states):
            state_probability += initial_dist[prior_state] * transition_table[prior_state][state]
        forward_probabilities[current_evidence][state] = state_probability * emission_table[state][given_current_evidence]

    # filling the forward probability table using the base probabilities calculated on previous step (basically using the notion of dynamic programming)
    for current_evidence in range(1, num_of_evidences):
        for current_state in range(num_of_states):
            state_possiblity = 0
            # calculating the possiblity of each state based on previous/prior state and transmission possiblities
            for prior_state in range(num_of_states):
                state_possiblity += forward_probabilities[current_evidence - 1][prior_state] * transition_table[prior_state][current_state]
            # calculating the probability using the probability of current state and given evidence (using emission table for given evidence info)
            forward_probabilities[current_evidence][current_state] = state_possiblity * emission_table[current_state][evidences[current_evidence]]

    # distribution of the last line is the distribution for Xt (normalization needed)
    prob_dist = normalize(forward_probabilities[-1])

    #print(prob_dist)
    print(f"<{prob_dist[0]:.2f}, {prob_dist[1]:.2f}>")

    return prob_dist


def likelihood_of_evidences(evidences):

    num_of_states = len(initial_dist)   # we have 2 states (rain, no rain) = length of given initial dist = len([prob0, 1 - prob0])      
    num_of_evidences = len(evidences)   # how many evidence we have observed/given
    
    # initialize forward prob table (each row will have two probabilities that corresponds to rain and not rain given evidence on that row)
    forward_probabilities = []  # forward probabilities will hold the probabilities we have calculated each step
    for i in range(num_of_evidences):
        forward_probabilities.append([0.0] * num_of_states)

    # base of forward probabilities table using the given first (0th) evidence of the evidence sequence (and also using the initial distributions <prob0>)
    current_evidence = 0
    for state in range(num_of_states):
        given_current_evidence = evidences[current_evidence]
        state_probability = 0
        for prior_state in range(num_of_states):
            state_probability += initial_dist[prior_state] * transition_table[prior_state][state]
        forward_probabilities[current_evidence][state] = state_probability * emission_table[state][given_current_evidence]

    # filling the forward probability table using the base probabilities calculated on previous step (basically using the notion of dynamic programming)
    for current_evidence in range(1, num_of_evidences):
        for current_state in range(num_of_states):
            state_possiblity = 0
            # calculating the possiblity of each state based on previous/prior state and transmission possiblities
            for prior_state in range(num_of_states):
                state_possiblity += forward_probabilities[current_evidence - 1][prior_state] * transition_table[prior_state][current_state]
            # calculating the probability using the probability of current state and given evidence (using emission table for given evidence info)
            forward_probabilities[current_evidence][current_state] = state_possiblity * emission_table[current_state][evidences[current_evidence]]

    # the sum of last line (rain | e1:t) + (not rain | e1:t) will have the probability of evidence sequence
    prob_of_sequence = sum(forward_probabilities[-1])

    print(f"<{forward_probabilities[-1][0]:.2f}, {forward_probabilities[-1][1]:.2f}>")

    return prob_of_sequence

def smoothing(evidences,k):

    num_of_states = len(initial_dist)   # we have 2 states (rain, no rain) = length of given initial dist = len([prob0, 1 - prob0])      
    num_of_evidences = len(evidences)   # how many evidence we have observed/given
    
    # initialize forward prob table (each row will have two probabilities that corresponds to rain and not rain given evidence on that row)
    forward_probabilities = []  # forward probabilities will hold the probabilities we have calculated each step
    for i in range(k):          # calculating forward algorithm table until k will be enough
        forward_probabilities.append([0.0] * num_of_states)

    # base of forward probabilities table using the given first (0th) evidence of the evidence sequence (and also using the initial distributions <prob0>)
    current_evidence = 0
    for state in range(num_of_states):
        given_current_evidence = evidences[current_evidence]
        state_probability = 0
        for prior_state in range(num_of_states):
            state_probability += initial_dist[prior_state] * transition_table[prior_state][state]
        forward_probabilities[current_evidence][state] = state_probability * emission_table[state][given_current_evidence]
    
    forward_probabilities[current_evidence] = normalize(forward_probabilities[current_evidence])

    # filling the forward probability table using the base probabilities calculated on previous step (basically using the notion of dynamic programming)
    for current_evidence in range(1, k):
        given_current_evidence = evidences[current_evidence]
        for current_state in range(num_of_states):
            state_possiblity = 0
            # calculating the possiblity of each state based on previous/prior state and transmission possiblities
            for prior_state in range(num_of_states):
                state_possiblity += forward_probabilities[current_evidence - 1][prior_state] * transition_table[prior_state][current_state]
            # calculating the probability using the probability of current state and given evidence (using emission table for given evidence info)
            forward_probabilities[current_evidence][current_state] = state_possiblity * emission_table[current_state][given_current_evidence]

    f_k = forward_probabilities[-1]     # prob dist of last line (k) of forwarding probability table

    backward_probabilities = []
    for i in range(num_of_evidences - k + 1):                   # calculating backward algorithm will have t-k rows (+1 from the base case of t+1)
        backward_probabilities.append([1.0] * num_of_states)     # initiating with 1.0 since the base case will be <1,1>

    # we can arrange the evidences we are dealing with in backward algorithm 
    # although we can calcualte the entire table (T * 2), calculating until t = k will be enough ((T-k) * 2)
        
    evidences = evidences[k:]
    num_of_evidences = len(evidences)
    
    for current_evidence in range(num_of_evidences-1,-1,-1):
        given_current_evidence = evidences[current_evidence]
        for current_state in range(num_of_states):
            state_possiblity = 0
            for posterior_state in range(num_of_states):
                state_possiblity += backward_probabilities[current_evidence+1][posterior_state] * transition_table[current_state][posterior_state] * emission_table[posterior_state][given_current_evidence]
            backward_probabilities[current_evidence][current_state] = state_possiblity
    
    b_k = backward_probabilities[0]     # since we've built the backward table from time = t to time = k  (backward) first (0th) row will be b_k

    # print(normalize(list_multiplication(f_k,b_k)))

    prob_dist = normalize(list_multiplication(f_k,b_k))
    print(f"<{prob_dist[0]:.2f}, {prob_dist[1]:.2f}>")


def most_likely_explanation(evidences):
    
    num_of_states = len(initial_dist)   # we have 2 states (rain, no rain) = length of given initial dist = len([prob0, 1 - prob0])      
    num_of_evidences = len(evidences)   # how many evidence we have observed/given
    
    # initialize forward prob table (each row will have two probabilities that corresponds to rain and not rain given evidence on that row)
    forward_probabilities = []  # forward probabilities will hold the probabilities we have calculated each step
    for i in range(num_of_evidences):
        forward_probabilities.append([0.0] * num_of_states)

    # base of forward probabilities table using the given first (0th) evidence of the evidence sequence (and also using the initial distributions <prob0>)
    current_evidence = 0
    for state in range(num_of_states):
        given_current_evidence = evidences[current_evidence]
        state_probability = 0
        for prior_state in range(num_of_states):
            state_probability += initial_dist[prior_state] * transition_table[prior_state][state]
        forward_probabilities[current_evidence][state] = state_probability * emission_table[state][given_current_evidence]

    forward_probabilities[current_evidence] = normalize(forward_probabilities[current_evidence])

    # filling the forward probability table using the base probabilities calculated on previous step (basically using the notion of dynamic programming)
    for current_evidence in range(1, num_of_evidences):
        given_current_evidence = evidences[current_evidence]
        for current_state in range(num_of_states):
            prior_state = forward_probabilities[current_evidence-1].index(max(forward_probabilities[current_evidence-1]))
            state_possiblity = forward_probabilities[current_evidence-1][prior_state] * transition_table[prior_state][current_state]
            # calculating the probability using the probability of current state and given evidence (using emission table for given evidence info)
            forward_probabilities[current_evidence][current_state] = state_possiblity * emission_table[current_state][given_current_evidence]
            
    print_mle(viterbi_path(forward_probabilities), forward_probabilities)

    return viterbi_path(forward_probabilities) , forward_probabilities

if algorithm_type == "F":
    filtering(queries)
elif algorithm_type == "L":
    likelihood_of_evidences(queries)
elif algorithm_type == "S":
    smoothing(queries,smoothing_k)
elif algorithm_type == "M":
    most_likely_explanation(queries)
