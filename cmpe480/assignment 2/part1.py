import sys
import copy
from queue import Queue

# getting input and preparing initial state
selected_agent = int(sys.argv[1])
input_file = sys.argv[2]
output_file = sys.argv[3]

input_state = []

with open(input_file, 'r') as input:
    for line in input:
        input_state.append([int(x) for x in line.split()])

f_out = open(output_file,"w")

class Node:
    def __init__(self, state, move = None, parent=None, children = [], evaluated = None, depth = None, agent = None):
        self.state = state
        self.move = move
        self.parent = parent
        self.children = children
        self.evaluated = evaluated
        self.depth = depth
        self.agent = agent
            
#####################

# helper function to print the output file
def output_printer(out,util,expanded_nodes,moves_to_guarantee):
    if moves_to_guarantee is None:
        moves_to_guarantee = 0
    out.write(f"{util}\n{expanded_nodes}\n{moves_to_guarantee}")

# helper function that prints the state (basically a matrix)
def print_state(state):
    #s = "\n"
    s = ""
    for i in range(3):
        row = map(str,state[i])
        row = " ".join(row)
        s += row + "\n"
    print(s)

# function that returns the position of the desired tile
def find_tile_position(tile, state):
    for i in range(3):
        for j in range(3):
            if state[i][j] == tile:
                return (i,j)
    # unsuccesful case
    return (-1,-1)

# returns true if given tile is blocking opponent's area
def is_blocking_two_rounds(tile, node):
    if node.parent is None or node.parent.parent is None or node.parent.parent.parent is None:
        return False
    
    grandparent_state = node.parent.state
    great_grandparent_state = node.parent.parent.parent.state

    if tile == 1 or tile == 2:
        blocking_conditions = [(2,1), (2,2)]

        (row1, col1) = find_tile_position(tile,grandparent_state)
        (row2, col2) = find_tile_position(tile,great_grandparent_state)

        if (row1,col1) == (row2,col2) and (row1,col1) in blocking_conditions:
            return True

    elif tile == 8 or tile == 9:
        blocking_conditions = [(0,0), (0,1)]

        (row1, col1) = find_tile_position(tile,grandparent_state)
        (row2, col2) = find_tile_position(tile,great_grandparent_state)

        if (row1,col1) == (row2,col2) and (row1,col1) in blocking_conditions:
            return True
    
    return False

# returns evaluation value (-1 or 1) if given agent does not move the blocking tile 
def bottom_right_blocking_evaluation(is_blocking, selected_agent = selected_agent):
    if is_blocking:
        if selected_agent == 1:
            return -1
        else:
            return 1
    else:
        return None

# returns evaluation value (-1 or 1) if given agent does not move the blocking tile 
def top_left_blocking_evaluation(is_blocking, selected_agent = selected_agent):
    if is_blocking:
        if selected_agent == 1:
            return 1
        else:
            return -1
    else:
        return None

# returns evaluation value depending on the selected agent if reverse move is made by one of the agents
def reverse_move_evaluation(agent, selected_agent = selected_agent):
    if agent == selected_agent: # selected agent is doing the forbidden move hence he loses
        return -1
    # other agent doing the forbidden move hence our selected agent wins
    return 1

# returns true if game is over (by default ending state)
def termination_check(state):
    top_left_condition = (state[0][0] == 1 and state[0][1] == 2) or (state[0][1] == 1 and state[0][0] == 2)
    bottom_right_condition = (state[2][1] == 8 and state[2][2] == 9) or (state[2][1] == 9 and state[2][2] == 8)
    if top_left_condition or bottom_right_condition:
        return True
    return False

# returns evaluation value when the game ends (by default ending state)
def evaluate(state, agent = selected_agent):
    top_left_condition = (state[0][0] == 1 and state[0][1] == 2) or (state[0][1] == 1 and state[0][0] == 2)
    bottom_right_condition = (state[2][1] == 8 and state[2][2] == 9) or (state[2][1] == 9 and state[2][2] == 8)

    if agent == 1:
        if top_left_condition:
            return 1
        elif bottom_right_condition:
            return -1
    elif agent == 2:
        if top_left_condition:
            return -1
        elif bottom_right_condition:
            return 1

# expands the given nodes and returns the successors (also assign evaluation value if successor is a leaf node)  
def expand_nodes(node):
    agent = selected_agent

    if node.agent == None:  # expanding initial node right now, first move is from selected_agent
        agent = selected_agent
    elif node.agent == 1:   # expanding a node obtained by agent1's move
        agent = 2
    elif node.agent == 2:   # expanding a node obtained by agent2's move
        agent = 1

    if termination_check(node.state):       # leaf node is entered in this function (should not expand this node)
        node.evaluated = evaluate(node.state)
        return []

    grandparent_move = ""   # holding this to make sure reverse move is not being made

    if node.parent is not None:
        grandparent_move = node.parent.move

    children = []

    if agent == 1:      # expand based on tile 1 and 2

        (row1 , col1) = find_tile_position(1,node.state)
        (row2 , col2) = find_tile_position(2,node.state)

        one_blocking = is_blocking_two_rounds(1,node)   # true if tile1 blocking opponent for two rounds
        two_blocking = is_blocking_two_rounds(2,node)   # true if tile2 blocking opponent for two rounds

        # move tile 1
        if row1 > 0 and node.state[row1-1][col1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row1-1][col1] , new_state[row1][col1] = new_state[row1][col1] , new_state[row1-1][col1]

            evaluated = bottom_right_blocking_evaluation(two_blocking)

            if evaluated is None and termination_check(new_state):   # tile 2 is not blocking opponent for two rounds 
                # new state (after moving the tile) is terminated state, then this specific child is a leaf
                evaluated = evaluate(new_state)

            if grandparent_move == "D1":        # reverse move is forbidden so this node will expand with evaluated value according to the rules
                evaluated = reverse_move_evaluation(agent)

            children.append(Node(new_state, "U1", node, depth=node.depth + 1, agent=agent, evaluated = evaluated))

        if col1 < 2 and node.state[row1][col1+1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row1][col1+1] , new_state[row1][col1] = new_state[row1][col1] , new_state[row1][col1+1]

            evaluated = bottom_right_blocking_evaluation(two_blocking)

            if evaluated is None and termination_check(new_state):
                evaluated = evaluate(new_state)
            
            if grandparent_move == "L1":        # reverse move is forbidden so this node will expand with evaluated value according to the rules
                evaluated = reverse_move_evaluation(agent)

            children.append(Node(new_state, "R1", node, depth=node.depth + 1, agent=agent, evaluated = evaluated))

        if row1 < 2 and node.state[row1+1][col1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row1+1][col1] , new_state[row1][col1] = new_state[row1][col1] , new_state[row1+1][col1]
            
            evaluated = bottom_right_blocking_evaluation(two_blocking)

            if evaluated is None and termination_check(new_state):
                evaluated = evaluate(new_state)

            if grandparent_move == "U1":        # reverse move is forbidden so this node will expand with evaluated value according to the rules
                evaluated = reverse_move_evaluation(agent)

            children.append(Node(new_state, "D1", node, depth=node.depth + 1, agent=agent, evaluated = evaluated))  

        if col1 > 0 and node.state[row1][col1-1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row1][col1-1] , new_state[row1][col1] = new_state[row1][col1] , new_state[row1][col1-1]

            evaluated = bottom_right_blocking_evaluation(two_blocking)

            if evaluated is None and termination_check(new_state):
                evaluated = evaluate(new_state)

            if grandparent_move == "R1":        # reverse move is forbidden so this node will expand with evaluated value according to the rules
                evaluated = reverse_move_evaluation(agent)

            children.append(Node(new_state, "L1", node, depth=node.depth + 1, agent=agent, evaluated = evaluated))
        
        # move tile 2 
        if row2 > 0 and node.state[row2-1][col2] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row2-1][col2] , new_state[row2][col2] = new_state[row2][col2] , new_state[row2-1][col2]

            evaluated = bottom_right_blocking_evaluation(one_blocking)

            if evaluated is None and termination_check(new_state):
                evaluated = evaluate(new_state)

            if grandparent_move == "D2":        # reverse move is forbidden so this node will expand with evaluated value according to the rules
                evaluated = reverse_move_evaluation(agent)

            children.append(Node(new_state, "U2", node, depth=node.depth + 1, agent=agent, evaluated = evaluated))

        if col2 < 2 and node.state[row2][col2+1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row2][col2+1] , new_state[row2][col2] = new_state[row2][col2] , new_state[row2][col2+1]

            evaluated = bottom_right_blocking_evaluation(one_blocking)

            if evaluated is None and termination_check(new_state):
                evaluated = evaluate(new_state)

            if grandparent_move == "L2":        # reverse move is forbidden so this node will expand with evaluated value according to the rules
                evaluated = reverse_move_evaluation(agent)

            children.append(Node(new_state, "R2", node, depth=node.depth + 1, agent=agent, evaluated = evaluated))

        if row2 < 2 and node.state[row2+1][col2] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row2+1][col2] , new_state[row2][col2] = new_state[row2][col2] , new_state[row2+1][col2]

            evaluated = bottom_right_blocking_evaluation(one_blocking)

            if evaluated is None and termination_check(new_state):
                evaluated = evaluate(new_state)

            if grandparent_move == "U2":        # reverse move is forbidden so this node will expand with evaluated value according to the rules
                evaluated = reverse_move_evaluation(agent)

            children.append(Node(new_state, "D2", node, depth=node.depth + 1, agent=agent, evaluated = evaluated))  

        if col2 > 0 and node.state[row2][col2-1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row2][col2-1] , new_state[row2][col2] = new_state[row2][col2] , new_state[row2][col2-1]

            evaluated = bottom_right_blocking_evaluation(one_blocking)

            if evaluated is None and termination_check(new_state):
                evaluated = evaluate(new_state)

            if grandparent_move == "R2":        # reverse move is forbidden so this node will expand with evaluated value according to the rules
                evaluated = reverse_move_evaluation(agent)

            children.append(Node(new_state, "L2", node, depth=node.depth + 1, agent=agent, evaluated = evaluated))
        
    elif agent == 2:      # expand based on tile 8 and 9

        (row8 , col8) = find_tile_position(8,node.state)
        (row9 , col9) = find_tile_position(9,node.state)

        eight_blocking = is_blocking_two_rounds(8,node) # true if tile8 blocking opponent for two rounds
        nine_blocking = is_blocking_two_rounds(9,node)  # true if tile9 blocking opponent for two rounds

        # move tile 8
        if row8 > 0 and node.state[row8-1][col8] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row8-1][col8] , new_state[row8][col8] = new_state[row8][col8] , new_state[row8-1][col8]

            evaluated = top_left_blocking_evaluation(nine_blocking)

            if evaluated is None and termination_check(new_state):
                evaluated = evaluate(new_state)

            if grandparent_move == "D8":        # reverse move is forbidden so this node will expand with evaluated value according to the rules
                evaluated = reverse_move_evaluation(agent)

            children.append(Node(new_state, "U8", node, depth=node.depth + 1, agent=agent,evaluated=evaluated))
            
        if col8 < 2 and node.state[row8][col8+1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row8][col8+1] , new_state[row8][col8] = new_state[row8][col8] , new_state[row8][col8+1]

            evaluated = top_left_blocking_evaluation(nine_blocking)

            if evaluated is None and termination_check(new_state):
                evaluated = evaluate(new_state)

            if grandparent_move == "L8":        # reverse move is forbidden so this node will expand with evaluated value according to the rules
                evaluated = reverse_move_evaluation(agent)

            children.append(Node(new_state, "R8", node, depth=node.depth + 1, agent=agent, evaluated = evaluated))

        if row8 < 2 and node.state[row8+1][col8] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row8+1][col8] , new_state[row8][col8] = new_state[row8][col8] , new_state[row8+1][col8]

            evaluated = top_left_blocking_evaluation(nine_blocking)

            if evaluated is None and termination_check(new_state):
                evaluated = evaluate(new_state)

            if grandparent_move == "U8":        # reverse move is forbidden so this node will expand with evaluated value according to the rules
                evaluated = reverse_move_evaluation(agent)

            children.append(Node(new_state, "D8", node, depth=node.depth + 1, agent=agent, evaluated = evaluated))  

        if col8 > 0 and node.state[row8][col8-1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row8][col8-1] , new_state[row8][col8] = new_state[row8][col8] , new_state[row8][col8-1]

            evaluated = top_left_blocking_evaluation(nine_blocking)

            if evaluated is None and termination_check(new_state):
                evaluated = evaluate(new_state)

            if grandparent_move == "R8":        # reverse move is forbidden so this node will expand with evaluated value according to the rules
                evaluated = reverse_move_evaluation(agent)

            children.append(Node(new_state, "L8", node, depth=node.depth + 1, agent=agent, evaluated = evaluated))
        
        # move tile 9
        if row9 > 0 and node.state[row9-1][col9] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row9-1][col9] , new_state[row9][col9] = new_state[row9][col9] , new_state[row9-1][col9]

            evaluated = top_left_blocking_evaluation(eight_blocking)

            if evaluated is None and termination_check(new_state):
                evaluated = evaluate(new_state)

            if grandparent_move == "D9":        # reverse move is forbidden so this node will expand with evaluated value according to the rules
                evaluated = reverse_move_evaluation(agent)

            children.append(Node(new_state, "U9", node, depth=node.depth + 1, agent=agent, evaluated = evaluated))

        if col9 < 2 and node.state[row9][col9+1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row9][col9+1] , new_state[row9][col9] = new_state[row9][col9] , new_state[row9][col9+1]

            evaluated = top_left_blocking_evaluation(eight_blocking)

            if evaluated is None and termination_check(new_state):
                evaluated = evaluate(new_state)

            if grandparent_move == "L9":        # reverse move is forbidden so this node will expand with evaluated value according to the rules
                evaluated = reverse_move_evaluation(agent)

            children.append(Node(new_state, "R9", node, depth=node.depth + 1, agent=agent, evaluated = evaluated))

        if row9 < 2 and node.state[row9+1][col9] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row9+1][col9] , new_state[row9][col9] = new_state[row9][col9] , new_state[row9+1][col9]

            evaluated = top_left_blocking_evaluation(eight_blocking)

            if evaluated is None and termination_check(new_state):
                evaluated = evaluate(new_state)

            if grandparent_move == "U9":        # reverse move is forbidden so this node will expand with evaluated value according to the rules
                evaluated = reverse_move_evaluation(agent)

            children.append(Node(new_state, "D9", node, depth=node.depth + 1, agent=agent, evaluated = evaluated))  

        if col9 > 0 and node.state[row9][col9-1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row9][col9-1] , new_state[row9][col9] = new_state[row9][col9] , new_state[row9][col9-1]

            evaluated = top_left_blocking_evaluation(eight_blocking)

            if evaluated is None and termination_check(new_state):
                evaluated = evaluate(new_state)

            if grandparent_move == "R9":        # reverse move is forbidden so this node will expand with evaluated value according to the rules
                evaluated = reverse_move_evaluation(agent)

            children.append(Node(new_state, "L9", node, depth=node.depth + 1, agent=agent, evaluated = evaluated))

    return children

# returns all branches where every node in that branch has the utility value equal to the root node
def find_paths(root):
    branches = []
    dfs(root, [], branches)
    return branches

# basic depth first search (used for finding winning/losing strategy if exists)
def dfs(node, current_branch, branches):
    if not node:
        return

    current_branch.append(node)

    if not node.children:  # If the node is a leaf
        branches.append(current_branch.copy())
    else:
        for child in node.children:
            if child.evaluated == node.evaluated:
                dfs(child, current_branch, branches)

    current_branch.pop()

# returns the max moves if there any guaranteed win/lose
def guarantee_result_moves(root):
    if root.evaluated == 0:
        return None
    
    guaranteed_branches = find_paths(root)
    max_depth = -1
    
    for branch in guaranteed_branches:
        if len(branch) > max_depth:
            max_depth = len(branch)
    
    # minus one (-1) since branches hold the number of nodes on that path, number of moves will be equal to (# of nodes - 1)
    return max_depth - 1

# ALGORITHM
def max_value(node):
    if node.evaluated != None:
        return node.evaluated
    
    node.evaluated = float('-inf')
    
    for child in node.children:
        node.evaluated = max(node.evaluated,min_value(child))
    
    return node.evaluated

def min_value(node):
    if node.evaluated != None:
        return node.evaluated
    
    node.evaluated = float('inf')

    for child in node.children:
        node.evaluated = min(node.evaluated, max_value(child))

    return node.evaluated
    

def minimax(input_state):
    fringe = Queue()
    initial_node = Node(input_state, depth = 0, agent = None)   # initial node's agent is none (no one make this move)
    fringe.put(initial_node)
    num_of_expanded_nodes = 1

    while not fringe.empty():
        processing_node = fringe.get()

        if processing_node.evaluated != None:    # this node is a leaf node - can't expand (determined during expand_nodes() function)
            continue

        # there are unterminated nodes at depth 10 -> turn them into leaf nodes (draw)
        if processing_node.depth == 10 and processing_node.evaluated == None:
            processing_node.evaluated = 0
            continue

        # stop bfs since depth limit is reached (this part is theoretically unreachable since we did not expand the nodes at d=10)
        if processing_node.depth == 11:
            break

        children = expand_nodes(processing_node)
        processing_node.children = children

        for node in children:
            fringe.put(node)
            num_of_expanded_nodes += 1

    util = max_value(initial_node)
    moves_to_guaranteed_result = guarantee_result_moves(initial_node)

    output_printer(f_out,util,num_of_expanded_nodes,moves_to_guaranteed_result)

minimax(input_state)

f_out.close()
