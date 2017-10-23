The file DJ.java shows how to read a bif file into a InferenceGraph object and do some basic manipulations with it.
You should be able to figure out the API from the files InferenceGraph.java, InferenceGraphNode.java

Here is a list of basic API calls to get you started:

Get the list of nodes in a InfereceGraph object G: Vector nodes=G.get_nodes();

Get parents of a node n: n.get_parents()

Get children of a node n: n.get_children()

Get the name of a node n:n.get_name() -> So to get a variable with a given name, simply loop over G.get_nodes() until you match the name.

Get the probability table at a node:n.get_Prob() -> Returns a probability function object;

Get the value of the probability table at a given node (node given its parents) for a given configuration of variables -> n.get_function_value(s)

where s is a String[][] object such that for each i, s[i][0] is the name of a variable in that table and s[i][1] is the value of that variable for which you want to get the probability.
