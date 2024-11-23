package main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Stack;

public class AStar {
	int start;
	int end;
	PlaneGraph graph;
	PriorityQueue<Integer> queue;
	Stack<NodeCard> stack;
	HashMap<Integer,NodeCard> nodes;	
	
	public AStar(PlaneGraph graph, int start, int end) {
		this.graph = graph;
		this.start = start;
		this.end = end;
		queue = new PriorityQueue<Integer>(new Distance());
		stack = new Stack<NodeCard>();
		nodes = new HashMap<>(graph.nodeCount());
	}
	
	public int[] path() {
		nodes.put(start, new NodeCard(start, start, 0));
		queue.add(start);
		while (queue.peek() != end) {
			visit(queue.poll());
			if (queue.isEmpty()) {
				return null;
			}
		}
		stack.push(nodes.get(end));
		
		ArrayList<Integer> path = new ArrayList<>();
		int node = end;
		for (NodeCard card = stack.pop(); !stack.isEmpty(); card = stack.pop()) {
			if (card.id == node) {
				path.add(0, node);
				node = card.thru;
			}
		}
		path.add(0, start);
		
		int[] intPath = new int[path.size()];
		for (int i = 0; i < path.size(); i++) {
			intPath[i] = path.get(i);
		}
		
		return intPath;
	}
	
	private void visit(int id) {
		for (int neighbor : graph.neighbors(id)) {
			nodes.get(id).peek(neighbor);
		}
		stack.push(nodes.get(id));
	}
	
	private class Distance implements Comparator<Integer> {
		public int compare(Integer id1, Integer id2) {
			if (nodes.get(id1).starDistance() < nodes.get(id2).starDistance()) {
				return -1;
			} else if (nodes.get(id1).starDistance() > nodes.get(id2).starDistance()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	private class NodeCard {
		int id;
		int thru;
		double dist;
		
		public NodeCard(int id, int thru, double dist) {
			this.id = id;
			this.thru = thru;
			this.dist = dist;
		}
		
		public void peek(int id) {
			nodes.putIfAbsent(id, new NodeCard(id, this.id, Double.MAX_VALUE));
			if (peekDistance(id) < nodes.get(id).dist) {
				nodes.put(id, new NodeCard(id, this.id, peekDistance(id)));
				if (!queue.contains(id)) {
					queue.add(id);
				}
			}
		}
		
		private double peekDistance(int id) {
			return dist + graph.distance(this.id, id);
		}
		
		public double starDistance() {
			return dist + graph.distance(id, end);
		}
		
		public String toString() {
			return "node: " + id + ", thru:" + thru + ", dist:" + dist;
		}
	}
}
