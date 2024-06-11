import sys
import copy

# getting input and preparing initial state
selected_agent = int(sys.argv[1])
input_file = sys.argv[2]
output_file = sys.argv[3]

input_state = []

with open(input_file, "r") as input:
    for line in input:
        input_state.append([int(x) for x in line.split()])

f_out = open(output_file,"w")

class Node:
    def __init__(self, state, move = None, parent=None, children = [], evaluated = None, depth = None, agent = None, last_moved_tile = None):
        self.state = state
        self.move = move
        self.parent = parent
        self.children = children
        self.evaluated = evaluated
        self.depth = depth
        self.agent = agent
        self.last_moved_tile = last_moved_tile

num_of_expanded_nodes = 0
            
#####################

# helper function to print the output file
def output_printer(out,util,expanded_nodes,moves_to_guarantee):
    # if there is no guarantee win/lose print 0
    if moves_to_guarantee is None:
        moves_to_guarantee = 0
    # does not print util and moves_to_guarantee since 1 value is wanted as stated in description
    # out.write(f"{util}\n{moves_to_guarantee}\n{expanded_nodes}")
    out.write(f"{expanded_nodes}")

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
    if node.parent is None or node.parent.parent is None or node.parent.parent.parent is None or node.parent.parent.parent.parent is None:
        return False
    
    grandparent = node.parent.parent
    great_grandparent = grandparent.parent.parent
    
    grandparent_state = grandparent.state
    great_grandparent_state = great_grandparent.state

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

# returns evaluation value depending on the selected agent if reverse move is made by one of the agents
def reverse_move_evaluation(agent, selected_agent = selected_agent):
    if agent == selected_agent: # selected agent is doing the forbidden move hence he loses
        return -1
    # other agent doing the forbidden move hence our selected agent wins
    return 1

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

# returns true (an evaluation value) if game is over (by default ending state)
def termination_check(state,node, selected_agent = selected_agent):
    top_left_condition = (state[0][0] == 1 and state[0][1] == 2) or (state[0][1] == 1 and state[0][0] == 2)
    bottom_right_condition = (state[2][1] == 8 and state[2][2] == 9) or (state[2][1] == 9 and state[2][2] == 8)

    # termination by correct tile positions

    if top_left_condition == True and selected_agent == 1:
        return 1
    elif top_left_condition == True and selected_agent == 2:
        return -1
    elif bottom_right_condition == True and selected_agent == 1:
        return -1
    elif bottom_right_condition == True and selected_agent == 2:
        return 1
    
    # termination by making reverse (forbidden) move

    reverse_moves = {"U1":"D1", "R1":"L1", "D1":"U1", "L1":"R1", "U2":"D2", "R2":"L2", "D2":"U2", "L2":"R2", "U8":"D8", "R8":"L8", "D8":"U8", "L8":"R8", "U9":"D9", "R9":"L9", "D9":"U9", "L9":"R9"}

    if node.parent is not None and node.parent.parent is not None:  # node have a grand parent
        grandparent_move = node.parent.parent.move
        if reverse_moves[node.move] == grandparent_move:
            if node.agent == 1 and selected_agent == 1:
                return -1
            elif node.agent == 1 and selected_agent == 2:
                return 1
            elif node.agent == 2 and selected_agent == 1:
                return 1
            elif node.agent == 2 and selected_agent == 2:
                return -1
    
    # termination by reaching depth limit
    
    if node.depth == 10:
        return 0
    
    # termination by NOT moving the tile that is blocking opponent for two rounds

    if node.agent == 1 and node.last_moved_tile == 1 and is_blocking_two_rounds(2,node):   # if tile2 blocking for two rounds and agent1 moved tile 1 terminate game
        if selected_agent == 1: # agent 1 loses, agent 2 wins
            return -1
        elif selected_agent == 2:
            return 1
    if node.agent == 1 and node.last_moved_tile == 2 and is_blocking_two_rounds(1,node):   # if tile1 blocking for two rounds and agent1 moved tile 2 terminate game
        if selected_agent == 1: # agent 1 loses, agent 2 wins
            return -1
        elif selected_agent == 2:
            return 1
        
    if node.agent == 2 and node.last_moved_tile == 8 and is_blocking_two_rounds(9,node):   # if tile9 blocking for two rounds and agent2 moved tile 8 terminate game
        if selected_agent == 1: # agent 1 wins, agent 2 loses
            return 1
        elif selected_agent == 2:
            return -1
    if node.agent == 2 and node.last_moved_tile == 9 and is_blocking_two_rounds(8,node):   # if tile8 blocking for two rounds and agent2 moved tile 9 terminate game
        if selected_agent == 1: # agent 1 loses, agent 2 wins
            return 1
        elif selected_agent == 2:
            return -1

    return None

# expands the given nodes and returns the successors (also assign evaluation value if successor is a leaf node)  
def expand_nodes(node):
    agent = selected_agent

    if node.agent == None:  # expanding initial node right now, first move is from selected_agent
        agent = selected_agent
    elif node.agent == 1:   # expanding a node obtained by agent1's move
        agent = 2
    elif node.agent == 2:   # expanding a node obtained by agent2's move
        agent = 1

    children = []

    if agent == 1:      # expand based on tile 1 and 2

        (row1 , col1) = find_tile_position(1,node.state)
        (row2 , col2) = find_tile_position(2,node.state)

        # move tile 1
        if row1 > 0 and node.state[row1-1][col1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row1-1][col1] , new_state[row1][col1] = new_state[row1][col1] , new_state[row1-1][col1]

            children.append(Node(new_state, "U1", node, depth=node.depth + 1, agent=agent, last_moved_tile=1))

        if col1 < 2 and node.state[row1][col1+1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row1][col1+1] , new_state[row1][col1] = new_state[row1][col1] , new_state[row1][col1+1]

            children.append(Node(new_state, "R1", node, depth=node.depth + 1, agent=agent, last_moved_tile=1))

        if row1 < 2 and node.state[row1+1][col1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row1+1][col1] , new_state[row1][col1] = new_state[row1][col1] , new_state[row1+1][col1]
            
            children.append(Node(new_state, "D1", node, depth=node.depth + 1, agent=agent, last_moved_tile=1))  

        if col1 > 0 and node.state[row1][col1-1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row1][col1-1] , new_state[row1][col1] = new_state[row1][col1] , new_state[row1][col1-1]

            children.append(Node(new_state, "L1", node, depth=node.depth + 1, agent=agent, last_moved_tile=1))
        
        # move tile 2 
        if row2 > 0 and node.state[row2-1][col2] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row2-1][col2] , new_state[row2][col2] = new_state[row2][col2] , new_state[row2-1][col2]

            children.append(Node(new_state, "U2", node, depth=node.depth + 1, agent=agent, last_moved_tile=2))

        if col2 < 2 and node.state[row2][col2+1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row2][col2+1] , new_state[row2][col2] = new_state[row2][col2] , new_state[row2][col2+1]

            children.append(Node(new_state, "R2", node, depth=node.depth + 1, agent=agent, last_moved_tile=2))

        if row2 < 2 and node.state[row2+1][col2] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row2+1][col2] , new_state[row2][col2] = new_state[row2][col2] , new_state[row2+1][col2]

            children.append(Node(new_state, "D2", node, depth=node.depth + 1, agent=agent, last_moved_tile=2))  

        if col2 > 0 and node.state[row2][col2-1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row2][col2-1] , new_state[row2][col2] = new_state[row2][col2] , new_state[row2][col2-1]

            children.append(Node(new_state, "L2", node, depth=node.depth + 1, agent=agent, last_moved_tile=2))
        
    elif agent == 2:      # expand based on tile 8 and 9

        (row8 , col8) = find_tile_position(8,node.state)
        (row9 , col9) = find_tile_position(9,node.state)

        # move tile 8
        if row8 > 0 and node.state[row8-1][col8] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row8-1][col8] , new_state[row8][col8] = new_state[row8][col8] , new_state[row8-1][col8]

            children.append(Node(new_state, "U8", node, depth=node.depth + 1, agent=agent, last_moved_tile=8))
            
        if col8 < 2 and node.state[row8][col8+1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row8][col8+1] , new_state[row8][col8] = new_state[row8][col8] , new_state[row8][col8+1]

            children.append(Node(new_state, "R8", node, depth=node.depth + 1, agent=agent, last_moved_tile=8))

        if row8 < 2 and node.state[row8+1][col8] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row8+1][col8] , new_state[row8][col8] = new_state[row8][col8] , new_state[row8+1][col8]

            children.append(Node(new_state, "D8", node, depth=node.depth + 1, agent=agent, last_moved_tile=8))  

        if col8 > 0 and node.state[row8][col8-1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row8][col8-1] , new_state[row8][col8] = new_state[row8][col8] , new_state[row8][col8-1]

            children.append(Node(new_state, "L8", node, depth=node.depth + 1, agent=agent, last_moved_tile=8))
        
        # move tile 9
        if row9 > 0 and node.state[row9-1][col9] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row9-1][col9] , new_state[row9][col9] = new_state[row9][col9] , new_state[row9-1][col9]

            children.append(Node(new_state, "U9", node, depth=node.depth + 1, agent=agent, last_moved_tile=9))

        if col9 < 2 and node.state[row9][col9+1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row9][col9+1] , new_state[row9][col9] = new_state[row9][col9] , new_state[row9][col9+1]

            children.append(Node(new_state, "R9", node, depth=node.depth + 1, agent=agent,last_moved_tile=9))

        if row9 < 2 and node.state[row9+1][col9] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row9+1][col9] , new_state[row9][col9] = new_state[row9][col9] , new_state[row9+1][col9]

            children.append(Node(new_state, "D9", node, depth=node.depth + 1, agent=agent, last_moved_tile=9))  

        if col9 > 0 and node.state[row9][col9-1] == 0:
            new_state = copy.deepcopy(node.state)
            new_state[row9][col9-1] , new_state[row9][col9] = new_state[row9][col9] , new_state[row9][col9-1]

            children.append(Node(new_state, "L9", node, depth=node.depth + 1, agent=agent, last_moved_tile=9))

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
        max_depth = max(len(branch), max_depth)
    
    # minus one (-1) since branches hold the number of nodes on that path, number of moves will be equal to (# of nodes - 1)
    return max_depth - 1

# ALGORITHM (including alpha-beta pruning)
def max_value(node, alpha, beta):
    global num_of_expanded_nodes

    val = termination_check(node.state, node)

    if val != None:
        node.evaluated = val
        return node.evaluated
    
    node.evaluated = float('-inf')

    successors = expand_nodes(node)
    node.children = successors
    
    for child in node.children:
        num_of_expanded_nodes += 1
        node.evaluated = max(node.evaluated,min_value(child,alpha,beta))
        if node.evaluated >= beta:
            return node.evaluated
        alpha = max(alpha, node.evaluated)
    
    return node.evaluated

def min_value(node, alpha,beta):
    global num_of_expanded_nodes

    val = termination_check(node.state, node)

    if val != None:
        node.evaluated = val
        return node.evaluated
    
    node.evaluated = float('inf')

    successors = expand_nodes(node)
    node.children = successors

    for child in node.children:
        num_of_expanded_nodes += 1
        node.evaluated = min(node.evaluated, max_value(child,alpha,beta))
        if node.evaluated <= alpha:
            return node.evaluated
        beta = min(beta, node.evaluated)

    return node.evaluated
      

def minimax(input_state):
    global num_of_expanded_nodes

    initial_node = Node(input_state, depth = 0, agent = None)   # initial node's agent is none (no one make this move)
    num_of_expanded_nodes = 1

    #alpha = float('-inf')
    #beta = float('inf')
    alpha = -1
    beta = 1
    util = max_value(initial_node,alpha,beta)
    moves_to_guaranteed_result = guarantee_result_moves(initial_node)

    output_printer(f_out,util,num_of_expanded_nodes,moves_to_guaranteed_result)

minimax(input_state)

f_out.close()
