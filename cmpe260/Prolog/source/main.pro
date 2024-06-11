% necdet can uzduran
% 2019400195
% compiling: yes
% complete: yes
:- ['cmpecraft.pro'].

:- init_from_map.

% 10 points
% manhattan_distance(+A, +B, -Distance) :- .
% 10 points
% minimum_of_list(+List, -Minimum) :- .
% 10 points
% find_nearest_type(+State, +ObjectType, -ObjKey, -Object, -Distance) :- .
% 10 points
% navigate_to(+State, +X, +Y, -ActionList, +DepthLimit) :- .
% 10 points
% chop_nearest_tree(+State, -ActionList) :- .
% 10 points
% mine_nearest_stone(+State, -ActionList) :- .
% 10 points
% gather_nearest_food(+State, -ActionList) :- .
% 10 points
% collect_requirements(+State, +ItemType, -ActionList) :- .
% 5 points
% find_castle_location(+State, -XMin, -YMin, -XMax, -YMax) :- .
% 15 points
% make_castle(+State, -ActionList) :- .

% //////////////////////////// PREDICATE 1 ////////////////////////////

manhattan_distance([X1,Y1],[X2,Y2],Distance) :- Distance is abs(X1-X2) + abs(Y1-Y2).

% //////////////////////////// PREDICATE 2 ////////////////////////////

minimum_of_list([Minimum],Minimum).
minimum_of_list([H,N|T],Minimum) :- H =< N,minimum_of_list([H|T],Minimum).
minimum_of_list([H,N|T],Minimum) :- N < H,minimum_of_list([N|T],Minimum).


% //////////////////////////// PREDICATE 3 ////////////////////////////

% get_distance_list predicate creates a list called DistanceList which contains the distances of the objects of the given type

get_distance_list(State,[TypeKey],GivenList,DistanceList) :- State = [AgentDict,ObjectDict,_], get_dict(TypeKey,ObjectDict,Object),
	get_dict(x,Object,Ox),get_dict(y,Object,Oy),get_dict(x,AgentDict,Ax),get_dict(y,AgentDict,Ay),manhattan_distance([Ox,Oy],[Ax,Ay],D),
	append(GivenList,[D],DistanceList).

get_distance_list(State,[TypeKeysHead|Tail],GivenList,DistanceList) :- State = [AgentDict,ObjectDict,_], get_dict(TypeKeysHead,ObjectDict,Object),
	get_dict(x,Object,Ox),get_dict(y,Object,Oy),get_dict(x,AgentDict,Ax),get_dict(y,AgentDict,Ay),manhattan_distance([Ox,Oy],[Ax,Ay],D),
	append(GivenList,[D],OutList),get_distance_list(State,Tail,OutList,DistanceList).

% extract object is the predicate that gives the information of the nearest object of the given type; key, object itself etc.

extract_object(State,[TypeKey],ObjKey,Object,Distance) :- State = [AgentDict,ObjectDict,_], get_dict(TypeKey,ObjectDict,CandidateObject),
	get_dict(x,CandidateObject,Ox),get_dict(y,CandidateObject,Oy),get_dict(x,AgentDict,Ax),get_dict(y,AgentDict,Ay),manhattan_distance([Ox,Oy],[Ax,Ay],D),
	((Distance = D) -> ObjKey is TypeKey,!,get_dict(TypeKey,ObjectDict,Object);fail).

extract_object(State,[TypeKey|Tail],ObjKey,Object,Distance) :- State = [AgentDict,ObjectDict,_], get_dict(TypeKey,ObjectDict,CandidateObject),
	get_dict(x,CandidateObject,Ox),get_dict(y,CandidateObject,Oy),get_dict(x,AgentDict,Ax),get_dict(y,AgentDict,Ay),manhattan_distance([Ox,Oy],[Ax,Ay],D),
	((Distance = D) -> ObjKey is TypeKey,!,get_dict(TypeKey,ObjectDict,Object);extract_object(State,Tail,ObjKey,Object,Distance)).
	

find_nearest_type(State,ObjectType,ObjKey,Object,Distance) :- State = [_,ObjectDict,_],findall(X,ObjectDict.X.type=ObjectType,TypeKeys),
	get_distance_list(State,TypeKeys,[],DistanceList),minimum_of_list(DistanceList,Distance),extract_object(State,TypeKeys,ObjKey,Object,Distance).
	
	
% //////////////////////////// PREDICATE 4 ////////////////////////////

% bottom 4 predicate are helper predicates for navigate_to predicate. L is the lateral or horizontal distance needed to go to reach the given point

append_goright(L,Count) :- length(L,Count),maplist(=(go_right),L).

append_goleft(L,Count) :- length(L,Count),maplist(=(go_left),L).

append_goup(L,Count) :- length(L,Count),maplist(=(go_up),L).

append_godown(L,Count) :- length(L,Count),maplist(=(go_down),L).

navigate_to(State, X, Y, _, DepthLimit) :- State = [AgentDict, _, _],get_dict(x,AgentDict,Ax),get_dict(y,AgentDict,Ay),
	Diff is abs(Ax - X) + abs(Ay - Y),Diff > DepthLimit,!,fail.

navigate_to(State, X, Y, ActionList, _) :- State = [AgentDict, _, _],get_dict(x,AgentDict,Ax),get_dict(y,AgentDict,Ay),
	((Ax =< X) -> (D is X - Ax , append_goright(ListX,D)) ; D is Ax - X , append_goleft(ListX,D)),
	((Ay =< Y) -> (D1 is Y - Ay , append_godown(ListY,D1)) ; D1 is Ay - Y , append_goup(ListY,D1)) , append(ListX,ListY,ActionList).

% //////////////////////////// PREDICATE 5 ////////////////////////////

% append leftclickc predicate is a helper predicate for chop tree , mine stone and gather food predicates, 
% L is the number of leftclick to be performed

append_leftclickc(L,Count) :- length(L,Count),maplist(=(left_click_c),L).

chop_nearest_tree(State, ActionList) :- find_nearest_type(State, tree, _, Object, Distance), get_dict(x,Object,Ox),get_dict(y,Object,Oy),
	navigate_to(State,Ox,Oy,NavigateList,Distance) , append_leftclickc(LeftclickList,4) , append(NavigateList,LeftclickList,ActionList).

% //////////////////////////// PREDICATE 6 ////////////////////////////

mine_nearest_stone(State, ActionList) :- find_nearest_type(State, stone, _, Object, Distance), get_dict(x,Object,Ox),get_dict(y,Object,Oy),
	navigate_to(State,Ox,Oy,NavigateList,Distance) , append_leftclickc(LeftclickList,4) , append(NavigateList,LeftclickList,ActionList).

mine_nearest_cobblestone(State, ActionList) :- find_nearest_type(State, cobblestone, _, Object, Distance), get_dict(x,Object,Ox),get_dict(y,Object,Oy),
	navigate_to(State,Ox,Oy,NavigateList,Distance) , append_leftclickc(LeftclickList,4) , append(NavigateList,LeftclickList,ActionList).

% //////////////////////////// PREDICATE 7 ////////////////////////////

gather_nearest_food(State, ActionList) :- find_nearest_type(State, food, _, Object, Distance), get_dict(x,Object,Ox),get_dict(y,Object,Oy),
	navigate_to(State,Ox,Oy,NavigateList,Distance) , append_leftclickc(LeftclickList,1) , append(NavigateList,LeftclickList,ActionList).

% //////////////////////////// PREDICATE 8 ////////////////////////////

% collect requirements
% collect stone and collect log are helper functions for collect requirements
% they create action lists that is to be performed to get enough stone or/and enough logs to craft the given ItemType

collect_requirements(State, ItemType, ActionList) :- (ItemType = stick) -> (collect_stick_helper(State,StickList), append(StickList,[],ActionList));
	(ItemType = stone_pickaxe) -> collect_stone_helper(State,StoneList),execute_actions(State,StoneList,NextState),
	collect_log_helper(NextState,LogList),append(StoneList,LogList,ActionList);
	(ItemType = stone_axe) -> collect_stone_helper(State,StoneList),execute_actions(State,StoneList,NextState),
	collect_log_helper(NextState,LogList),append(StoneList,LogList,ActionList).
	
collect_log_helper(State,LogList) :- State = [AgentDict, _, _], get_dict(inventory,AgentDict,Envanter),
	(not(get_dict(stick,Envanter,_)) , ((not(get_dict(log,Envanter,_)), NumLog is 0) ; get_dict(log,Envanter,NumLog),NumLog<5)) -> 
	NeededLog is 5-NumLog , NeededTree is ceiling(NeededLog/3) , chop_tree(NeededTree,State,List1),append(List1,[craft_stick],LogList) ;
	(get_dict(stick,Envanter,NumStick), NumStick<2 , ((not(get_dict(log,Envanter,_)), NumLog is 0) ; get_dict(log,Envanter,NumLog),NumLog<5)) ->
	NeededLog is 5-NumLog , NeededTree is ceiling(NeededLog/3) , chop_tree(NeededTree,State,List2),append(List2,[craft_stick],LogList) ;
	(get_dict(stick,Envanter,NumStick), NumStick >= 2 , (not(get_dict(log,Envanter,_)); get_dict(log,Envanter,NumLog),NumLog<3)) ->
	chop_tree(1,State,LogList) ;
	append([],[],LogList).
	
collect_stone_helper(State,StoneList) :- State = [AgentDict, _, _], get_dict(inventory,AgentDict,Envanter),
	((not(get_dict(cobblestone,Envanter,_)),NumCobble is 0) ; (get_dict(cobblestone,Envanter,NumCobble), NumCobble < 3)) -> 
	(find_nearest_type(State,stone,_,_,_) -> mine_nearest_stone(State,StoneList); NeededCobble is 3-NumCobble,
	mine_cobblestone(NeededCobble,State,StoneList)) ; append([],[],StoneList).

mine_cobblestone(1,State,ActionList) :- mine_nearest_cobblestone(State,ActionList).
mine_cobblestone(2,State,ActionList) :- mine_nearest_cobblestone(State,List1),execute_actions(State, List1, NextState1),
	mine_nearest_cobblestone(NextState1,List2),append(List1,List2,ActionList).
mine_cobblestone(3,State,ActionList) :- mine_nearest_cobblestone(State,List1),execute_actions(State, List1, NextState1),
	mine_nearest_cobblestone(NextState1,List2),execute_actions(NextState1, List2, NextState2),mine_nearest_cobblestone(NextState2,List3),
	append(List1,List2,TempList),append(TempList,List3,ActionList).

chop_tree(1,State,ActionList) :- chop_nearest_tree(State,ActionList).
chop_tree(2,State,ActionList) :- chop_nearest_tree(State,List1),execute_actions(State, List1, NextState1),
	chop_nearest_tree(NextState1,List2),append(List1,List2,ActionList).

collect_stick_helper(State,StickList) :- State = [AgentDict, _, _], get_dict(inventory,AgentDict,Envanter),
	(not(get_dict(log,Envanter,_)) ; (get_dict(log,Envanter,NumLog), NumLog < 2)) -> chop_nearest_tree(State,StickList); append([],[],StickList).


% //////////////////////////// PREDICATE 9 ////////////////////////////

% true if the tile is occupied , false otherwise

is_tile_occupied(X,Y,State) :- State = [_, StateDict, _],get_dict(_, StateDict, Object),
get_dict(x, Object, Ox),get_dict(y, Object, Oy),get_dict(type, Object, _),X = Ox, Y = Oy.

% check if 3x3 tile is available for the given X,Y which are the coordinates for the up_left corner of the 3x3 block

nine_checker(State,X,Y) :-  X1 is X+1,X2 is X+2,Y1 is Y+1,Y2 is Y+2,not(is_tile_occupied(X,Y,State)),not(is_tile_occupied(X1,Y,State)),
	not(is_tile_occupied(X2,Y,State)),not(is_tile_occupied(X,Y1,State)),not(is_tile_occupied(X1,Y1,State)),not(is_tile_occupied(X2,Y1,State)),
	not(is_tile_occupied(X,Y2,State)),not(is_tile_occupied(X1,Y2,State)),not(is_tile_occupied(X2,Y2,State)).

% searches for the available Xmin and Ymin for the castle , by convention it starts searching from 1,1

give_locations(State,X,Y,Xmin,Ymin) :- RightLimit is X+2,DownLimit is Y+2,width(W),height(H),
	RightLimit =< W-2 , DownLimit =< H-2,nine_checker(State,X,Y) -> Xmin is X, Ymin is Y ;
	RightLimit is X+2,DownLimit is Y+2,width(W),height(H),
	RightLimit =< W-2 , DownLimit =< H-2,not(nine_checker(State,X,Y)) ->  IncrementedX is X+1,give_locations(State,IncrementedX,Y,Xmin,Ymin);
	RightLimit is X+2,DownLimit is Y+2,width(W),height(H),
	RightLimit > W-2, DownLimit =< H-2 -> IncrementedY is Y+1, give_locations(State,1,IncrementedY,Xmin,Ymin);
	DownLimit is Y+2,height(H),
	DownLimit > H-2 -> fail.
	
find_castle_location(State, XMin, YMin, XMax, YMax) :- give_locations(State,1,1,XMin,YMin),XMax is XMin+2,YMax is YMin+2.

% //////////////////////////// PREDICATE 10 ////////////////////////////

% creates a list of keys of given item type , Num is number of objects that is in the map of given type

keys_of_type(State,ItemType,Bag,Num) :- State = [_,ObjectDict,_],findall(X,ObjectDict.X.type=ItemType,Bag),length(Bag,Num).

% stone for castle creates an action list that is needed to be performed to get L number of cobblestone for castle

stone_for_castle(_,GivenList,0,ActionList) :- append(GivenList,[],ActionList),!.
stone_for_castle(State,GivenList,L,ActionList) :- mine_nearest_stone(State,List),execute_actions(State,List,NewState),
	append(GivenList,List,Temp),M is L-1,stone_for_castle(NewState,Temp,M,ActionList).

% cobblestone for castle creates an action list that is needed to be performed to get L number of cobblestone for castle

cobblestone_for_castle(_,GivenList,0,ActionList) :- append(GivenList,[],ActionList),!.
cobblestone_for_castle(State,GivenList,L,ActionList) :- mine_nearest_cobblestone(State,List),execute_actions(State,List,NewState),
	append(GivenList,List,Temp),M is L-1,cobblestone_for_castle(NewState,Temp,M,ActionList).

% placing cobbles creates an action list to be performed to build a castle, basically produces an action list which is needed to be performed to
% build a 3x3 castle vy placing 9 cobblestones starting from north east and ending at center

placing_cobbles(Given,OutList) :- append(Given,[place_ne],Temp0),append(Temp0,[place_e],Temp1),append(Temp1,[place_se],Temp2),
	append(Temp2,[place_s],Temp3),append(Temp3,[place_sw],Temp4),append(Temp4,[place_w],Temp5),append(Temp5,[place_nw],Temp6),
	append(Temp6,[place_n],Temp7),append(Temp7,[place_c],OutList).

% mining actions produces an action list which is needed to be performed to get the needed number of cobblestone for castle

mining_actions(State,CobbleNeeded,StoneinMap,_,ActionList) :- StoneNeededTobeMined is ceiling(CobbleNeeded/3),
	(StoneinMap >= StoneNeededTobeMined -> stone_for_castle(State,[],StoneNeededTobeMined,ActionList);
	CobbleNeededTobeMined is CobbleNeeded-(3*StoneinMap),stone_for_castle(State,[],StoneinMap,StoneList),execute_actions(State,StoneList,NState),
	cobblestone_for_castle(NState,[],CobbleNeededTobeMined,CobbleList),append(StoneList,CobbleList,ActionList)).

make_castle(State, ActionList) :- State = [A,_,_],get_dict(inventory,A,Inv),width(W),height(H),D is W+H,placing_cobbles([],PlacingList),
	(not(get_dict(cobblestone,Inv,_)),CobbleHave is 0; get_dict(cobblestone,Inv,CobbleHave)),CobbleNeeded is 9-CobbleHave,
	keys_of_type(State,stone,_,StoneinMap),keys_of_type(State,cobblestone,_,CobbleinMap),
	(CobbleNeeded =< 0 -> find_castle_location(State, XMin, YMin, XMax, YMax),Xmid is (XMin+XMax)/2,Ymid is (YMin+YMax)/2,
	navigate_to(State,Xmid,Ymid,NavList,D),append(NavList,PlacingList,ActionList);
	mining_actions(State,CobbleNeeded,StoneinMap,CobbleinMap,MiningList),execute_actions(State,MiningList,N1state),
	find_castle_location(N1state, XMin, YMin, XMax, YMax),Xmid is (XMin+XMax)/2,Ymid is (YMin+YMax)/2,
	navigate_to(N1state,Xmid,Ymid,NavList,D),append(MiningList,NavList,TempList),append(TempList,PlacingList,ActionList)).
	
% //////////////////////////// ///////////////////////////////////////////////////////////////////// ////////////////////////////
	
	
% FILLER PREDICATES FOR DEBUGGING MAIN PREDICATES 
% maker(GivenList,0,ActionList) :- append(GivenList,[],ActionList),!.
% maker(GivenList,L,ActionList) :- append(GivenList,[1],Temp),M is L-1,maker(Temp,M,ActionList).

% stone_for_castle(State,ActionList,1,NewState) :- mine_nearest_stone(State,ActionList),execute_actions(State,ActionList,NewState).
% stone_for_castle(State,ActionList,2,NewState) :- mine_nearest_stone(State,List1),execute_actions(State,List1,NewState1),
%	mine_nearest_stone(NewState1,List2),append(List1,List2,ActionList),execute_actions(NewState1,List2,NewState).
% stone_for_castle(State,ActionList,3,NewState) :- mine_nearest_stone(State,List1),execute_actions(State,List1,NewState1),mine_nearest_stone(NewState1,List2),
%	execute_actions(NewState1,List2,NewState2),mine_nearest_stone(NewState2,List3),append(List1,List2,Temp),append(Temp,List3,ActionList),
%	execute_actions(NewState2,List3,NewState).
