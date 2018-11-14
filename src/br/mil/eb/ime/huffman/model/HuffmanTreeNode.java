package br.mil.eb.ime.huffman.model;

public class HuffmanTreeNode implements Comparable<HuffmanTreeNode> {

	//ATRIBUTOS
	private String characters;
	private int frequency;
	
	//CONSTRUTOR
	public HuffmanTreeNode(String characters, int frequency) {
		this.characters = characters;
		this.frequency = frequency;
	}

	//GETTERS E SETTERS
	public String getCharacters() {
		return characters;
	}

	public void setCharacters(String characters) {
		this.characters = characters;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	//IMPLEMENTAÇÃO DO MÉTODO DA INTERFACE Comparable
	@Override
	public int compareTo(HuffmanTreeNode other) {
		if(this.frequency <= other.frequency)
			return -1;
		else
			return 1;
	}
}