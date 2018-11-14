package br.mil.eb.ime.huffman.model;

import java.io.Serializable;

public class CharacterFrequency implements Serializable {

	//ID DE SERIALIZAÇÃO
	private static final long serialVersionUID = -162305251865869363L;

	//ATRIBUTOS
	private char character;
	private short frequency;
	
	//CONSTRUTOR
	public CharacterFrequency(char character, short frequency) {
		this.character = character;
		this.frequency = frequency;
	}

	//GETTERS E SETTERS
	public char getCharacter() {
		return character;
	}

	public void setCharacter(char character) {
		this.character = character;
	}

	public short getFrequency() {
		return frequency;
	}

	public void setFrequency(short frequency) {
		this.frequency = frequency;
	}
	
	//OUTROS MÉTODOS PÚBLICOS
	public void incrementFrequency() {
		frequency++;
	}
}