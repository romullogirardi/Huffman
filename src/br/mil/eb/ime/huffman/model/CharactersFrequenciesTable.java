package br.mil.eb.ime.huffman.model;

import java.io.Serializable;
import java.util.ArrayList;

public class CharactersFrequenciesTable implements Serializable {

	//ID DE SERIALIZAÇÃO
	private static final long serialVersionUID = -2639848022497500883L;

	//ATRIBUTOS
	private ArrayList<CharacterFrequency> charactersFrequencies;
	private int numberOfHuffmanBits;
	
	//CONSTRUTOR
	public CharactersFrequenciesTable(ArrayList<CharacterFrequency> charactersFrequencies, int numberOfHuffmanBits) {
		this.charactersFrequencies = charactersFrequencies;
		this.numberOfHuffmanBits = numberOfHuffmanBits;
	}
	
	//GETTERS E SETTERS
	public ArrayList<CharacterFrequency> getCharactersFrequencies() {
		return charactersFrequencies;
	}
	
	public void setCharactersFrequencies(ArrayList<CharacterFrequency> charactersFrequencies) {
		this.charactersFrequencies = charactersFrequencies;
	}
	
	public int getNumberOfHuffmanBits() {
		return numberOfHuffmanBits;
	}
	
	public void setNumberOfHuffmanBits(int numberOfHuffmanBits) {
		this.numberOfHuffmanBits = numberOfHuffmanBits;
	}
}