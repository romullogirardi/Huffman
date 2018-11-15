package br.mil.eb.ime.huffman.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class HuffmanManager {

	//CONSTANTES
	private final int MARK_CHAR_INDEX = 65279;
	private final String FREQUENCIES_TABLE_FILE_NAME = "frequenciesTable.szb";
	private final String HUFFMAN_SEQUENCE_FILE_NAME = "huffmanSequence.txt";
	
	//IMPLEMENTAÇÃO COMO SINGLETON
	private static HuffmanManager instance = null;
	public static HuffmanManager getInstance() {
		if(instance == null)
			instance = new HuffmanManager();
		return instance;
	}

	//MÉTODO DE COMPRESSÃO
	public String compress(File sourceTxtFile, File destinationZipFile) {
		
		//Ler conteúdo do arquivo .txt
		String txtContent = readTxtFile(sourceTxtFile).trim();
		System.out.println("Conteúdo do arquivo .txt: " + txtContent);
		
		//Montar a tabela de frequências
		ArrayList<CharacterFrequency> charactersFrequencies = new ArrayList<>();
		for(Character character : txtContent.toCharArray()) {
			if(character != null && ((int) character != MARK_CHAR_INDEX)) {
				CharacterFrequency characterFrequency = getCharacterFrequency(charactersFrequencies, character);
				if(characterFrequency != null)
					characterFrequency.incrementFrequency();
				else {
					characterFrequency = new CharacterFrequency(character, (short) 1);
					charactersFrequencies.add(characterFrequency);
				}
			}
		}
		System.out.println("\nTabela de frequências:\n");
		for(CharacterFrequency characterFrequency : charactersFrequencies) {
			System.out.println(characterFrequency.getCharacter() + " (ASCII " + (int) characterFrequency.getCharacter() + ") => " + characterFrequency.getFrequency());
		}
		
		//Montar a sequência de Huffman
		String huffmanSequence = generateHuffmanSequence(charactersFrequencies, txtContent);
		System.out.println("\nSequência de Huffman gerada: " + huffmanSequence);

		//Adicionar o número de bits de Huffman à tabela
		CharactersFrequenciesTable frequenciesTable = new CharactersFrequenciesTable(charactersFrequencies, huffmanSequence.length());
		
		//Salvar a tabela em um arquivo
		String frequenciesTableFilePath = destinationZipFile.getParent() + "/" + FREQUENCIES_TABLE_FILE_NAME;
		saveFile(frequenciesTableFilePath, frequenciesTable);

		//Salvar a sequência de Huffman em um arquivo
		String huffmanSequenceFilePath = destinationZipFile.getParent() + "/" + HUFFMAN_SEQUENCE_FILE_NAME;
		try {
			Files.write(Paths.get(huffmanSequenceFilePath), fromBinaryStringToByteArray(huffmanSequence), StandardOpenOption.CREATE);
		} catch (IOException e) {
			System.out.println("Erro na leitura da sequência de Huffman em arquivo!");
			e.printStackTrace();
			return null;
		}
		
		//Colocar arquivos no arquivo .zip de destino
		String[] srcFiles = {frequenciesTableFilePath, huffmanSequenceFilePath};
		String destinationZipFilePath = destinationZipFile.getAbsolutePath();
		if(!destinationZipFilePath.contains(".zip"))
			destinationZipFilePath += ".zip";
		if(!zipFiles(destinationZipFilePath, srcFiles))
			return null;
		
		//Montar mensagem de feedback da compressão 
		String message = "COMPRESSÃO REALIZADA COM SUCESSO!\n\n";
		message += "Detalhes da compressão:\n";
		long sourceTxtFileSize = sourceTxtFile.length();
		message += "- Tamanho do texto original: " + sourceTxtFileSize + " bytes\n";
		long huffmanSequenceFileSize = new File(huffmanSequenceFilePath).length();
		message += "- Tamanho do texto comprimido por Huffman: " + huffmanSequenceFileSize + " bytes\n";
		long frequenciesTableFileSize = new File(frequenciesTableFilePath).length();
		message += "- Tamanho da tabela de frequências de Huffman: " + frequenciesTableFileSize + " bytes\n";
//		long destinationZipFileSize = new File(destinationZipFilePath).length();
//		message += "- Tamanho do arquivo .zip de Huffman: " + destinationZipFileSize + " bytes\n";
		float compression = ((float)(huffmanSequenceFileSize - sourceTxtFileSize) / sourceTxtFileSize) * 100;
		message += "- COMPRESSÃO OBTIDA (original X comprimido): " + String.format("%.2f", compression) + "%\n";
		
		//Apagar os arquivos colocados no .zip
		new File(frequenciesTableFilePath).delete();
		new File(huffmanSequenceFilePath).delete();
		
		return message;
	}

	//MÉTODO DE DESCOMPRESSÃO
	public boolean decompress(File sourceZipFile, File destinationTxtFile) {
		
		//Descomprimir o arquivo .zip
		unzipFiles(sourceZipFile.getAbsolutePath(), sourceZipFile.getParent());
		String frequenciesTableFilePath = sourceZipFile.getParent() + "/" + FREQUENCIES_TABLE_FILE_NAME;
		String huffmanSequenceFilePath = sourceZipFile.getParent() + "/" + HUFFMAN_SEQUENCE_FILE_NAME;
		
		//Ler a tabela de frequências do arquivo
		CharactersFrequenciesTable frequenciesTable = readFile(frequenciesTableFilePath);

		//Ler a sequência de Huffman do arquivo
		byte[] byteArray;
		try {
			byteArray = Files.readAllBytes(Paths.get(huffmanSequenceFilePath));
		} catch (IOException e) {
			System.out.println("Erro na leitura da sequência de Huffman!");
			e.printStackTrace();
			return false;
		}
		String huffmanSequence = fromByteArrayToBinaryString(byteArray, frequenciesTable.getNumberOfHuffmanBits());
		System.out.println("Conteúdo da sequência de Huffman: " + huffmanSequence);

		
		//Imprimir a tabela de frequ^wncias lida
		System.out.println("\nTabela de frequências:\n");
		for(CharacterFrequency characterFrequency : frequenciesTable.getCharactersFrequencies()) {
			System.out.println(characterFrequency.getCharacter() + " (ASCII " + (int) characterFrequency.getCharacter() + ") => " + characterFrequency.getFrequency());
		}

		
		//Apagar os arquivos extraídos
		new File(frequenciesTableFilePath).delete();
		new File(huffmanSequenceFilePath).delete();
		
		//Traduzir a sequência de Huffman
		String textSequence = generateTextSequence(frequenciesTable.getCharactersFrequencies(), huffmanSequence);
		System.out.println("\nTexto gerado: " + textSequence);
		
		//Salvar a sequência traduzida no arquivo de destino
		String destinationTxtFilePath = destinationTxtFile.getAbsolutePath();
		if(!destinationTxtFilePath.contains(".txt"))
			destinationTxtFilePath += ".txt";
		try {
			Files.write(Paths.get(destinationTxtFilePath), textSequence.getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			System.out.println("Erro na escrita do texto descomprimido em arquivo!");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	//MÉTODOS DE LEITURA E ESCRITA EM ARQUIVO
	private String readTxtFile(File txtFile) {
		
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(txtFile.getAbsolutePath().toString()), "UTF8"));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String text = sb.toString();
		    br.close();
		    return text;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public CharactersFrequenciesTable readFile(String filePath) {
		SerializedPersistenceUtils fileManipulator = new SerializedPersistenceUtils(new File(filePath));
		Object object = fileManipulator.readObject();
		if (object != null && (object instanceof CharactersFrequenciesTable)) {
			return (CharactersFrequenciesTable) object;
		}
		return null;
	}

	public void saveFile(String filePath, Object object) {
		SerializedPersistenceUtils fileManipulator = new SerializedPersistenceUtils(new File(filePath));
		fileManipulator.saveObject(object);
	}

	//MÉTODOS DE ZIP E UNZIP
	private boolean zipFiles(String zipFile, String[] srcFiles) {
		try {
			byte[] buffer = new byte[1024];
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);
			for (int i=0; i < srcFiles.length; i++) {
				File srcFile = new File(srcFiles[i]);
				FileInputStream fis = new FileInputStream(srcFile);
				zos.putNextEntry(new ZipEntry(srcFile.getName()));
				int length;
				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
				zos.closeEntry();
				fis.close();
			}
			zos.close();
			return true;
		}
		catch (IOException ioe) {
			return false;
		}
	}
	
	private boolean unzipFiles(String inputZip, String destinationDirectory) {
		
	    int BUFFER = 2048;
	    File sourceZipFile = new File(inputZip);
	    File unzipDestinationDirectory = new File(destinationDirectory);
	    unzipDestinationDirectory.mkdir();

	    ZipFile zipFile;
	    try {
			zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);
		} catch (IOException e) {
			System.out.println("Erro na abertura do arquivo .zip!");
			e.printStackTrace();
			return false;
		}

	    @SuppressWarnings("rawtypes")
		Enumeration zipFileEntries = zipFile.entries();

	    while (zipFileEntries.hasMoreElements()) {
	        ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
	        String currentEntry = entry.getName();
	        File destFile = new File(unzipDestinationDirectory, currentEntry);
	        destFile = new File(unzipDestinationDirectory, destFile.getName());

	        File destinationParent = destFile.getParentFile();

	        destinationParent.mkdirs();

	        try {
	            if (!entry.isDirectory()) {
	                BufferedInputStream is = new BufferedInputStream(zipFile.getInputStream(entry));
	                int currentByte;
	                byte data[] = new byte[BUFFER];

	                FileOutputStream fos = new FileOutputStream(destFile);
	                BufferedOutputStream dest =
	                        new BufferedOutputStream(fos, BUFFER);

	                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
	                    dest.write(data, 0, currentByte);
	                }
	                dest.flush();
	                dest.close();
	                is.close();
	            }
	        } catch (IOException ioe) {
	            ioe.printStackTrace();
	        }
	    }
	    try {
			zipFile.close();
		} catch (IOException e) {
			System.out.println("Erro no fechamento do arquivo .zip!");
			e.printStackTrace();
			return false;
		}
	    
	    return true;
	}
	
	//MÉTODOS DE CONVERSÃO STRING (DE 0s E 1s) <=> ByteArray
	private byte[] fromBinaryStringToByteArray(String binaryString) {
		
		BitSet bitSet = new BitSet(binaryString.length());
		int bitcounter = 0;
		for(Character character : binaryString.toCharArray()) {
		    if(character.equals('1')) {
		        bitSet.set(bitcounter);
		    }
		    bitcounter++;
		}
		return bitSet.toByteArray();
	}
	
	private String fromByteArrayToBinaryString(byte[] byteArray, int numberOfBits) {
		
		if(numberOfBits > (byteArray.length * 8))
			return "";
		
		String binaryString = "";
		BitSet bitSet = BitSet.valueOf(byteArray);
		for(int index = 0; index < numberOfBits; index++) {
		    if(bitSet.get(index)) {
		        binaryString += "1";
		    } else {
		        binaryString += "0";
		    }
		}
		return binaryString;
	}
	
	//MÉTODOS RELACIONADOS AO ALGORITMO DE HUFFMAN
	private String generateHuffmanSequence(List<CharacterFrequency> charactersFrequencies, String textSequence) {
		
		String huffmanSequence = new String();
		Map<Character, String> huffmanTreeCodification = generateHuffmanTreeCodification(charactersFrequencies);
		for(Character character : textSequence.toCharArray()) {
			String codification = huffmanTreeCodification.get(character);
			if(codification != null)
				huffmanSequence += codification;
		}
		return huffmanSequence;
	}
	
	private String generateTextSequence(List<CharacterFrequency> charactersFrequencies, String huffmanSequence) {
		
		String textSequence = new String();
		Map<Character, String> huffmanTreeCodification = generateHuffmanTreeCodification(charactersFrequencies);
		String buffer = new String();
		for(Character digit : huffmanSequence.toCharArray()) {
			buffer += digit;
			if(huffmanTreeCodification.values().contains(buffer)) {
				for(Character character : huffmanTreeCodification.keySet()) {
					if(huffmanTreeCodification.get(character).equals(buffer)) {
						textSequence += character;
						break;
					}
				}
				buffer = "";
			}
			else
				continue;
		}
		return textSequence;
	}
	
	private Map<Character, String> generateHuffmanTreeCodification(List<CharacterFrequency> charactersFrequencies) {

		//Inicialização da árvore a partir das frequências dos caracteres
		ArrayList<HuffmanTreeNode> huffmanTree = new ArrayList<>();
		for(CharacterFrequency characterFrequency : charactersFrequencies) {
			huffmanTree.add(new HuffmanTreeNode(characterFrequency.getCharacter() + "", characterFrequency.getFrequency()));
		}

		//Inicialização do codificador a partir das frequências dos caracteres
		Map<Character, String> huffmanTreeCodification = new HashMap<>();
		for(CharacterFrequency characterFrequency : charactersFrequencies) {
			huffmanTreeCodification.put(characterFrequency.getCharacter(), "");
		}

		//Montagem da árvore/codificação
		int stepCounter = 1;
		while(huffmanTree.size() > 1) {
			
			//Ordenar os nós da árvore em ordem crescente de frequência
			Collections.sort(huffmanTree);
			System.out.println("\nÁrvore de Huffman (PASSO " + stepCounter + "):\n");
			for(HuffmanTreeNode node : huffmanTree) {
				System.out.println(node.getCharacters() + " => " + node.getFrequency());
			}
			
			//Fundir os dois nós menos frequentes
			huffmanTree.add(new HuffmanTreeNode(
					huffmanTree.get(0).getCharacters() + huffmanTree.get(1).getCharacters(), 
					huffmanTree.get(0).getFrequency() + huffmanTree.get(1).getFrequency()));
			
			//Computar o 1º nó fundido
			for(char character : huffmanTree.get(0).getCharacters().toCharArray()) {
				huffmanTreeCodification.put(character, "0" + huffmanTreeCodification.get(character));
			}

			//Computar o 2º nó fundido
			for(char character : huffmanTree.get(1).getCharacters().toCharArray()) {
				huffmanTreeCodification.put(character, "1" + huffmanTreeCodification.get(character));
			}
			
			//Remover os nós fundidos da árvore
			huffmanTree.remove(0);
			huffmanTree.remove(0);
			
			//Incrementar o contador de passos
			stepCounter++;
		}
		
		System.out.println("\nTabela de codificação:\n");
		for(Character character : huffmanTreeCodification.keySet()) {
			System.out.println(character + " => " + huffmanTreeCodification.get(character));
		}

		return huffmanTreeCodification;
	}
	
	private CharacterFrequency getCharacterFrequency(List<CharacterFrequency> charactersFrequencies, char character) {
		for(CharacterFrequency characterFrequency : charactersFrequencies) {
			if(characterFrequency.getCharacter() == character)
				return characterFrequency;
		}
		return null;
	}
}