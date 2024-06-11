import csv
import math
import random

node_id = 0

with open('car_evaluation.csv', 'r') as file:
    reader = csv.reader(file)
    header = next(reader)
    data = list(reader)

random.seed(480)        # seed is assigned to a value in order to produce the same result on different runs
random.shuffle(data)

buying_prices = [row[0] for row in data]
unique_buying_prices = set(buying_prices)

maintenance = [row[1] for row in data]
unique_maintenance = set(maintenance)

doors = [row[2] for row in data]
unique_doors = set(doors)

persons = [row[3] for row in data]
unique_persons = set(persons)

lug_boot = [row[4] for row in data]
unique_lug_boot = set(lug_boot)

safety = [row[5] for row in data]
unique_safety = set(safety)

decision_class = [row[6] for row in data]
unique_decision_class = set(decision_class)

attributes = ["b", "m", "d", "p", "l", "s"]

attribute_vals = {"b" : unique_buying_prices, "m" : unique_maintenance, "d" : unique_doors, "p" : unique_persons, "l" : unique_lug_boot, "s" : unique_safety}

rows = data     # used "rows" instead of data throughout development process in order to increase visualization while coding

###

class Node:
    def __init__(self, data = None, children = [], parent = None, selection = None, selection_value = None, possible_selections = [], decision = None, depth = 0, id = 0):
        self.data = data
        self.children = children
        self.parent = parent
        self.selection = selection
        self.selection_value = selection_value
        self.possible_selections = possible_selections
        self.decision = decision
        self.depth = depth
        self.id = id
    
def print_node(node):
    print("Node :\n")
    print(f"Data Size: {len(node.data)}, Parent: {node.parent.selection} {node.parent.selection_value} \nAttribute Selection: {node.selection}, Attribute Value : {node.selection_value}, \nPossible Selections: {node.possible_selections}, Decision: {node.decision}, Depth: {node.depth}\n")

###

def return_entropy_values(node):            # return all entropy values for all possible attributes node can have (attribute has not assigned to the node)
    pairs = []  # pairs is a list of tuples (attribute, entropy)

    for attribute in node.possible_selections:
        dc_seperation, size = seperate(attribute,node.data)
        entropy = calculate_entropy(dc_seperation, size)
        pairs.append((attribute,entropy))

    print(pairs)

def get_entropy_of_single_node(node):   # returns the entropy value of the node that has been assigned with attribute and attribute value
    entropy = 0
    num_total = len(node.data)
    for dc in unique_decision_class:
        count = sum(1 for row in node.data if row[-1] == dc)
        if count > 0:
            prob = (count / num_total)
            entropy += -(prob * math.log2(prob))

    return entropy

def decision_of_row(row, root):
    attribute_values = {"b" : row[0], "m" : row[1], "d" : row[2], "p" : row[3], "l" : row[4], "s" : row[5]}

    current_node = root

    while current_node.decision == None:
        child_selection = current_node.children[0].selection
        for child in current_node.children:
            if child.selection_value == attribute_values[child_selection]:
                current_node = child
                break

    return current_node.decision

def validation_accuracy(root,validation_set):
    count = 0
    for row in validation_set:
        if decision_of_row(row, root) == row[-1]:
            count += 1

    return (count / len(validation_set))

def seperate(attribute,data):
    # returns : dict( dict() ) where each key is attribute value of the given attribute and value is dictionary where each key is one of decision class value 
    # and value corresponds to each key is the number of rows that equals to the key
    
    # Example : given "b" as attribute

    # {'vhigh': {'vgood': 0, 'acc': 72, 'good': 0, 'unacc': 360}, 
    # 'med': {'vgood': 26, 'acc': 115, 'good': 23, 'unacc': 268}, 
    # 'low': {'vgood': 39, 'acc': 89, 'good': 46, 'unacc': 258}, 
    # 'high': {'vgood': 0, 'acc': 108, 'good': 0, 'unacc': 324}}

    decision_class_seperation = dict()

    if attribute == "b":
        for attribute_value in unique_buying_prices:
            value = dict()
            for dc in unique_decision_class:
                count = sum(1 for row in data if row[0] == attribute_value and row[-1] == dc)
                value[dc] = count
            decision_class_seperation[attribute_value] = value

    elif attribute == "m":
        for attribute_value in unique_maintenance:
            value = dict()
            for dc in unique_decision_class:
                count = sum(1 for row in data if row[1] == attribute_value and row[-1] == dc)
                value[dc] = count
            decision_class_seperation[attribute_value] = value

    elif attribute == "d":
        for attribute_value in unique_doors:
            value = dict()
            for dc in unique_decision_class:
                count = sum(1 for row in data if row[2] == attribute_value and row[-1] == dc)
                value[dc] = count
            decision_class_seperation[attribute_value] = value

    elif attribute == "p":
        for attribute_value in unique_persons:
            value = dict()
            for dc in unique_decision_class:
                count = sum(1 for row in data if row[3] == attribute_value and row[-1] == dc)
                value[dc] = count
            decision_class_seperation[attribute_value] = value

    elif attribute == "l":
        for attribute_value in unique_lug_boot:
            value = dict()
            for dc in unique_decision_class:
                count = sum(1 for row in data if row[4] == attribute_value and row[-1] == dc)
                value[dc] = count
            decision_class_seperation[attribute_value] = value

    elif attribute == "s":
        for attribute_value in unique_safety:
            value = dict()
            for dc in unique_decision_class:
                count = sum(1 for row in data if row[5] == attribute_value and row[-1] == dc)
                value[dc] = count
            decision_class_seperation[attribute_value] = value

    return decision_class_seperation, len(data)

def extract_rows(attribute, attribute_value, data):
    # Given an attribute and attribute value returns the rows where given attribute (column) has the value of given attribute value. 
    # (returns filtered rows)

    # given "b" as "vhigh"
    # [
    # ['vhigh', 'vhigh', '2', '2', 'med', 'med', 'unacc'], 
    # ['vhigh', 'low', '2', 'more', 'med', 'low', 'unacc'], 
    # ['vhigh', 'low', '5more', 'more', 'small', 'med', 'unacc'], 
    # ['vhigh', 'low', '4', '2', 'big', 'low', 'unacc'],
    # ...
    # ]

    extracted_rows = []

    if attribute == "b":
        extracted_rows = [row for row in data if row[0] == attribute_value]
    elif attribute == "m":
        extracted_rows = [row for row in data if row[1] == attribute_value]
    elif attribute == "d":
        extracted_rows = [row for row in data if row[2] == attribute_value]
    elif attribute == "p":
        extracted_rows = [row for row in data if row[3] == attribute_value]
    elif attribute == "l":
        extracted_rows = [row for row in data if row[4] == attribute_value]
    elif attribute == "s":
        extracted_rows = [row for row in data if row[5] == attribute_value]

    return extracted_rows


def calculate_entropy(dc_seperation, num_of_instances): # num_of_instances : number of instances that parent node have
    # idea : calculate each children's entropy and get weighted average of child entropies

    # returns : weighted average of the entropies of nodes given dc_seperation and data size

    entropies = []
    instance_probs = []

    for seperation in dc_seperation.values():
        num_of_seperated_instances = sum(seperation.values())
        instance_probs.append(num_of_seperated_instances / num_of_instances)
        entropy = 0
        for instance in seperation.values():
            if(instance > 0):
                prob = instance / num_of_seperated_instances
                entropy += -(prob * math.log2(prob))
        entropies.append(entropy)
    w_avg = 0
    for i in range(len(entropies)):
        w_avg += entropies[i] * instance_probs[i]

    return w_avg

def expand_node(parent):
    min_entropy_attribute = ""
    min_entropy_value = float("inf")
    children = []

    for attribute in parent.possible_selections:
        dc_seperation, size = seperate(attribute,parent.data)
        entropy = calculate_entropy(dc_seperation, size)
        if entropy < min_entropy_value:
            min_entropy_value = entropy
            min_entropy_attribute = attribute

    child_attribute_selection = min_entropy_attribute
    child_remanining_possible_selections = [selection for selection in parent.possible_selections if selection != min_entropy_attribute]

    for attribute_value in attribute_vals[min_entropy_attribute]:
        child_data = extract_rows(min_entropy_attribute, attribute_value, parent.data)
        child = Node(data=child_data, parent = parent, selection=child_attribute_selection, selection_value=attribute_value, possible_selections= child_remanining_possible_selections, depth = parent.depth + 1)
        children.append(child)

    return children

def leaf_check(node):
    if get_entropy_of_single_node(node) == 0 or node.possible_selections == []:
        return True
    return False

def get_decision(node):
    rows = node.data
    d = {"unacc" : 0, "acc" : 1, "good" : 2, "vgood" : 3}
    rev_d = {0 : "unacc", 1 : "acc", 2 : "good", 3 : "vgood"}
    count_list = [0,0,0,0] # will hold the count of "unacc", "acc", "good", "vgood" (decision classes)

    for row in rows:
        ind = d[row[-1]]
        count_list[ind] += 1
    
    max_index = count_list.index(max(count_list))

    return rev_d[max_index]

def build_decision_tree(initial_node, max_depth = float("inf")):

    # max_depth can be adjusted if wanted to tune

    fringe = []
    fringe.append(initial_node)
    node_id = 0
    visited = set()

    while len(fringe) > 0:
        processing_node = fringe.pop(0)
        processing_node.id = node_id
        node_id += 1

        if leaf_check(processing_node) or processing_node.depth == max_depth:
            processing_node.decision = get_decision(processing_node)
            continue
        
        visited.add(tuple(map(tuple, (processing_node.data + processing_node.children + processing_node.possible_selections))))

        children = expand_node(processing_node)

        processing_node.children = children

        for node in children:
            if tuple(map(tuple, (node.data + node.children + node.possible_selections))) not in visited:
                fringe.append(node)    


def cross_validation(data):
    best_tree_root = None

    # each partition consists 300 sample data
    partition1 = data[:300]
    partition2 = data[300:600]
    partition3 = data[600:900]
    partition4 = data[900:1200]
    partition5 = data[1200:1500]
    # test set consists 228 sample data (will be used as unseen data to evaluate performance)
    test_set = data[1500:]

    ##

    validation_set_1 = partition1  
    training_set_1 = partition2 + partition3 + partition4  + partition5
    initial_node_1 = Node(data=training_set_1, possible_selections=attributes, depth = 0)
    build_decision_tree(initial_node_1)
    acc_1 = validation_accuracy(initial_node_1, validation_set_1)
    #print(test_accuracy(initial_node_1, test_set_1))

    ##

    validation_set_2 = partition2  
    training_set_2 =  partition1 + partition3 + partition4 + partition5
    initial_node_2 = Node(data=training_set_2, possible_selections=attributes, depth = 0)
    build_decision_tree(initial_node_2)
    acc_2 = validation_accuracy(initial_node_2, validation_set_2)
    
    ##

    validation_set_3 = partition3  
    training_set_3 =  partition1 + partition2 + partition4 + partition5
    initial_node_3 = Node(data=training_set_3, possible_selections=attributes, depth = 0)
    build_decision_tree(initial_node_3)
    acc_3 = validation_accuracy(initial_node_3, validation_set_3)

    ##

    validation_set_4 = partition4
    training_set_4 = partition1 + partition2 + partition3 + partition5
    initial_node_4 = Node(data=training_set_4, possible_selections=attributes, depth = 0)
    build_decision_tree(initial_node_4)
    acc_4 = validation_accuracy(initial_node_4, validation_set_4)

    ##

    validation_set_5 = partition5
    training_set_5 =  partition1 + partition2 + partition3 + partition4
    initial_node_5 = Node(data=training_set_5, possible_selections=attributes, depth = 0)
    build_decision_tree(initial_node_5)
    acc_5 = validation_accuracy(initial_node_5, validation_set_5)

    accuracies = [acc_1, acc_2, acc_3, acc_4, acc_5]
    nodes = [initial_node_1, initial_node_2, initial_node_3, initial_node_4, initial_node_5]
    best_accuracy = max(accuracies)
    best_tree_root = nodes[accuracies.index(best_accuracy)]

    return best_tree_root , best_accuracy

# MAIN

best_tree_root , best_accuracy = cross_validation(rows)

print(f"Best decision tree's accuracy : {best_accuracy}")
