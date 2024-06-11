import sys
import copy
from queue import Queue
import heapq

#####################

# getting input and preparing initial states
input_file = sys.argv[1]
output_file = sys.argv[2]

out = open(output_file, "w")

input_state = []

goal_state = [[1,2,3],[4,5,6],[0,0,0]]

with open(input_file, 'r') as input:
    for line in input:
        input_state.append([int(x) for x in line.split()])

# assign symbols to zeros to differentiate them (01 -> first blank tile, 02 -> second blank tile, 03 -> third blank tile)

count = 0 # which blank tile I'm dealing with

for i in range(3):
    for j in range(3):
        if input_state[i][j] == 0:
            if count == 0:
                input_state[i][j] = "01"
                count += 1
            elif count == 1:
                input_state[i][j] = "02"
                count += 1
            elif count == 2:
                input_state[i][j] = "03"



#####################

class Node:
    def __init__(self, state, move = None, parent=None, heuristic = 0, cost = 0):
        self.state = state
        self.move = move
        self.parent = parent
        self.heuristic = heuristic
        self.cost = cost

    def __lt__(self, other):
        if self.heuristic + self.cost < other.heuristic + other.cost:
            return True
        elif self.heuristic + self.cost >= other.heuristic + other.cost:
            return False
        # no need for U-R-D-L order
    
#####################

def get_state(state):
    resulting_matrix = ""
    for i in range(3):
        row = ""
        for j in range(3):
            row += str(state[i][j]) + " "
        resulting_matrix += row.strip() + "\n"
    resulting_matrix += "\n"
    return resulting_matrix

def reset_zeros(state):
    for i in range(3):
        for j in range(3):
            if state[i][j] == "01" or state[i][j] == "02" or state[i][j] == "03":
                state[i][j] = 0
    return state
    

def zeros_position(state,expanding_node):    # find the position of kth blank tile
    for i in range(3):
        for j in range(3):
            if state[i][j] == expanding_node:
                return (i,j)
    # unsuccessful case
    return (-1,-1)

def find_tile_position(tile, state):
    for i in range(3):
        for j in range(3):
            if state[i][j] == tile:
                return (i,j)
    # unsuccesful case
    return (-1,-1)

def total_manhattan_distance(current_state, goal_state = goal_state):
    tiles = [1,2,3,4,5,6]
    total_manhattan = 0

    for tile in tiles:
        (x0, y0) = find_tile_position(tile, goal_state)     # place the tile should have been
        (x1, y1) = find_tile_position(tile, current_state)  # place the tile is currently in
        manhattan = abs(x1 - x0) + abs(y1 - y0)
        total_manhattan += manhattan
    
    return total_manhattan

def expand_nodes(node):

    children = []

    for i in range(3): # we need to deal with 3 blank tiles
        
        blank_tile = str(i+1)           # which blank tile is currently expanding
        expanding_node = "0" + blank_tile

        (row , col) = zeros_position(node.state,expanding_node)    # find the ith unexpanded zero (0 - 1 - 2)

        if row > 0:
            new_state = copy.deepcopy(node.state)
            new_state[row-1][col] , new_state[row][col] = new_state[row][col] , new_state[row-1][col]
            children.append(Node(new_state, "U" + blank_tile, node))
        if col < 2:
            new_state = copy.deepcopy(node.state)
            new_state[row][col+1] , new_state[row][col] = new_state[row][col] , new_state[row][col+1]
            children.append(Node(new_state, "R" + blank_tile, node))
        if row < 2:
            new_state = copy.deepcopy(node.state)
            new_state[row+1][col] , new_state[row][col] = new_state[row][col] , new_state[row+1][col]
            children.append(Node(new_state, "D" + blank_tile, node))  
        if col > 0:
            new_state = copy.deepcopy(node.state)
            new_state[row][col-1] , new_state[row][col] = new_state[row][col] , new_state[row][col-1]
            children.append(Node(new_state, "L" + blank_tile, node))

    return children

def is_goal_state(current_state):
    if current_state[0] == [1,2,3] and current_state[1] == [4,5,6] and current_state[2] in [["01","02","03"],["01","03","02"],["02","01","03"],["02","03","01"],["03","02","01"],["03","01","02"]]:
        return True
    return False


def a_star(input_state, goal_state):
    fringe = []     # should behave fringe as a priority queue - based on heuristic value + path cost   
    initial_node = Node(input_state, heuristic=total_manhattan_distance(input_state, goal_state), cost=0)
    fringe.append(initial_node)
    visited = set()
    closed = set()
    num_of_expanded_nodes = 1

    closed.add(tuple(map(tuple, initial_node.state)))

    while len(fringe) > 0:
        processing_node = heapq.heappop(fringe)

        # goal test = if last row is all blank tiles independent of the order of blank tiles, goal is reached
        if is_goal_state(processing_node.state):
            print_output(processing_node,num_of_expanded_nodes)
            return 1

        visited.add(tuple(map(tuple, processing_node.state)))

        children = expand_nodes(processing_node)

        for child in children:
            child.heuristic = total_manhattan_distance(child.state, goal_state)
            child.cost = processing_node.cost + 1

        for node in children:
            if tuple(map(tuple, node.state)) not in visited and tuple(map(tuple, node.state)) not in closed:
                heapq.heappush(fringe, node)
                closed.add(tuple(map(tuple, node.state)))
                num_of_expanded_nodes += 1

    return -1

def print_output(node, num_of_expanded_nodes, output = out):
    path_cost = -1  # included starting state while printing hence for arithmetic reasons started with -1 in this part
    path = ""
    while(node != None):
        path_cost += 1
        path = get_state(reset_zeros(node.state)) + path
        node = node.parent
    output.write(f"{num_of_expanded_nodes}\n{path_cost}\n{path.strip()}")

def main():
    a_star(input_state, goal_state)

main()

out.close()