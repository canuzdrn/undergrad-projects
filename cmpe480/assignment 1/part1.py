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

goal_state = [[1,2,3],[4,5,6],[7,8,0]]

with open(input_file, 'r') as input:
    for line in input:
        input_state.append([int(x) for x in line.split()])

#####################

class Node:
    def __init__(self, state, move = None, parent=None, heuristic = 0, cost = 0, external_sorting = False, insertion_order = 0):
        self.state = state
        self.move = move
        self.parent = parent
        self.heuristic = heuristic
        self.cost = cost
        self.external_sorting = external_sorting
        self.insertion_order = insertion_order

    def __lt__(self, other):
        if self.heuristic + self.cost < other.heuristic + other.cost:
            return True
        elif self.heuristic + self.cost > other.heuristic + other.cost:
            return False
        elif (self.heuristic + self.cost == other.heuristic + other.cost) and self.external_sorting == True and self.move != other.move:
            move_vals = {'U': 0, 'R': 1, 'D': 2, 'L': 3}
            return move_vals[self.move] < move_vals[other.move]
        else:
            return self.insertion_order < other.insertion_order
            
#####################

def total_manhattan_distance(current_state, goal_state = goal_state):
    tiles = [1,2,3,4,5,6,7,8]
    total_manhattan = 0

    for tile in tiles:
        (x0, y0) = find_tile_position(tile, goal_state)     # place the tile should have been
        (x1, y1) = find_tile_position(tile, current_state)  # place the tile is currently in
        manhattan = abs(x1 - x0) + abs(y1 - y0)
        total_manhattan += manhattan
    
    return total_manhattan


def zeros_position(state):
    for i in range(3):
        for j in range(3):
            if state[i][j] == 0:
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

def expand_nodes(node, dfs_expansion = False):

    (row , col) = zeros_position(node.state)

    children = []

    # external sorting is being inhereted by the child node from parent node

    if not dfs_expansion:
        if row > 0:
            new_state = copy.deepcopy(node.state)
            new_state[row-1][col] , new_state[row][col] = new_state[row][col] , new_state[row-1][col]
            children.append(Node(new_state, "U", node, external_sorting= node.external_sorting))
        if col < 2:
            new_state = copy.deepcopy(node.state)
            new_state[row][col+1] , new_state[row][col] = new_state[row][col] , new_state[row][col+1]
            children.append(Node(new_state, "R", node, external_sorting= node.external_sorting))
        if row < 2:
            new_state = copy.deepcopy(node.state)
            new_state[row+1][col] , new_state[row][col] = new_state[row][col] , new_state[row+1][col]
            children.append(Node(new_state, "D", node, external_sorting= node.external_sorting))  
        if col > 0:
            new_state = copy.deepcopy(node.state)
            new_state[row][col-1] , new_state[row][col] = new_state[row][col] , new_state[row][col-1]
            children.append(Node(new_state, "L", node, external_sorting= node.external_sorting))
    else:
        if col > 0:
            new_state = copy.deepcopy(node.state)
            new_state[row][col-1] , new_state[row][col] = new_state[row][col] , new_state[row][col-1]
            children.append(Node(new_state, "L", node, external_sorting= node.external_sorting))
        if row < 2:
            new_state = copy.deepcopy(node.state)
            new_state[row+1][col] , new_state[row][col] = new_state[row][col] , new_state[row+1][col]
            children.append(Node(new_state, "D", node, external_sorting= node.external_sorting))  
        if col < 2:
            new_state = copy.deepcopy(node.state)
            new_state[row][col+1] , new_state[row][col] = new_state[row][col] , new_state[row][col+1]
            children.append(Node(new_state, "R", node, external_sorting= node.external_sorting))
        if row > 0:
            new_state = copy.deepcopy(node.state)
            new_state[row-1][col] , new_state[row][col] = new_state[row][col] , new_state[row-1][col]
            children.append(Node(new_state, "U", node, external_sorting= node.external_sorting))
        
        
    return children

#####################

def bfs(input_state, goal_state):
    fringe = Queue()
    initial_node = Node(input_state)
    fringe.put(initial_node)
    visited = set()
    num_of_expanded_nodes = 1

    closed = set()
    closed.add(tuple(map(tuple, initial_node.state)))

    while not fringe.empty():
        processing_node = fringe.get()

        if processing_node.state == goal_state:
            print_output(processing_node,num_of_expanded_nodes)
            return 1
        
        visited.add(tuple(map(tuple, processing_node.state)))

        children = expand_nodes(processing_node)

        for node in children:
            if tuple(map(tuple, node.state)) not in visited and tuple(map(tuple, node.state)) not in closed:
                fringe.put(node)
                closed.add(tuple(map(tuple, node.state)))
                num_of_expanded_nodes += 1

    # unsuccessful case
    return -1

def dfs(input_state, goal_state):
    fringe = []
    initial_node = Node(input_state)
    fringe.append(initial_node)
    visited = set()
    num_of_expanded_nodes = 1

    closed = set()
    closed.add(tuple(map(tuple, initial_node.state)))


    while len(fringe) > 0:
        processing_node = fringe.pop()

        if processing_node.state == goal_state:
            print_output(processing_node,num_of_expanded_nodes)
            return 1
        
        visited.add(tuple(map(tuple, processing_node.state)))

        children = expand_nodes(processing_node, dfs_expansion=True)

        for node in children:
            if tuple(map(tuple, node.state)) not in visited and tuple(map(tuple, node.state)) not in closed:
                fringe.append(node)
                closed.add(tuple(map(tuple, node.state)))
                num_of_expanded_nodes += 1
    
    return -1

def uniform_cost(input_state, goal_state):
    fringe = []     # should behave fringe as a priority queue - based on path cost value    
    initial_node = Node(input_state, cost= 0, external_sorting=True, insertion_order=len(fringe))
    fringe.append(initial_node)
    counter = 1
    visited = set()
    num_of_expanded_nodes = 1

    closed = set()
    closed.add(tuple(map(tuple, initial_node.state)))

    while len(fringe) > 0:
        processing_node = heapq.heappop(fringe)

        if processing_node.state == goal_state:
            print_output(processing_node,num_of_expanded_nodes)
            return 1

        visited.add(tuple(map(tuple, processing_node.state)))

        children = expand_nodes(processing_node)

        for child in children:
            child.cost = processing_node.cost + 1

        for node in children:
            if tuple(map(tuple, node.state)) not in visited and tuple(map(tuple, node.state)) not in closed:
                num_of_expanded_nodes += 1
                node.insertion_order = counter
                heapq.heappush(fringe, node)
                closed.add(tuple(map(tuple, node.state)))
                counter += 1
    return -1

def greedy_search(input_state, goal_state):
    fringe = []     # should behave fringe as a priority queue - based on heuristic value    
    initial_node = Node(input_state, heuristic=total_manhattan_distance(input_state, goal_state), external_sorting=True)
    fringe.append(initial_node)
    counter = 1
    visited = set()
    num_of_expanded_nodes = 1

    closed = set()
    closed.add(tuple(map(tuple, initial_node.state)))

    while len(fringe) > 0:
        processing_node = heapq.heappop(fringe)

        if processing_node.state == goal_state:
            print_output(processing_node,num_of_expanded_nodes)
            return 1

        visited.add(tuple(map(tuple, processing_node.state)))

        children = expand_nodes(processing_node)

        for child in children:
            child.heuristic = total_manhattan_distance(child.state, goal_state)

        for node in children:
            if tuple(map(tuple, node.state)) not in visited and tuple(map(tuple, node.state)) not in closed:
                num_of_expanded_nodes += 1
                node.insertion_order = counter
                heapq.heappush(fringe, node)
                closed.add(tuple(map(tuple, node.state)))
                counter += 1

    return -1


def a_star(input_state, goal_state):
    fringe = []     # should behave fringe as a priority queue - based on heuristic value + path cost   
    initial_node = Node(input_state, heuristic=total_manhattan_distance(input_state, goal_state), cost=0,external_sorting=True,insertion_order=len(fringe))
    fringe.append(initial_node)
    counter = 1
    visited = set()
    num_of_expanded_nodes = 1

    closed = set()
    closed.add(tuple(map(tuple, initial_node.state)))

    while len(fringe) > 0:
        processing_node = heapq.heappop(fringe)

        if processing_node.state == goal_state:
            print_output(processing_node,num_of_expanded_nodes)
            return 1

        visited.add(tuple(map(tuple, processing_node.state)))

        children = expand_nodes(processing_node)

        for child in children:
            child.heuristic = total_manhattan_distance(child.state, goal_state)
            child.cost = processing_node.cost + 1

        for node in children:
            if tuple(map(tuple, node.state)) not in visited and tuple(map(tuple, node.state)) not in closed:
                num_of_expanded_nodes += 1
                node.insertion_order = counter
                heapq.heappush(fringe, node)
                closed.add(tuple(map(tuple, node.state)))
                counter += 1

    return -1

def print_output(node, num_of_expanded_nodes, output = out):
    path = ""
    path_cost = 0
    while(node.move != None):
        path = node.move + " " + path
        path_cost += 1
        node = node.parent
    path = path.strip()
    output.write(f"{num_of_expanded_nodes}\n{path_cost}\n{path}\n")


def main():
    bfs(input_state, goal_state)

    dfs(input_state, goal_state)

    uniform_cost(input_state, goal_state)

    greedy_search(input_state, goal_state)

    a_star(input_state, goal_state)

main()

out.close()