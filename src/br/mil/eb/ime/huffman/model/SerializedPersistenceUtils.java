package br.mil.eb.ime.huffman.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializedPersistenceUtils {
	
	//ATRIBUTO
	protected File file;
	
	//CONSTRUTOR
	public SerializedPersistenceUtils(File file){
		this.file = file;
		// Cria um novo arquivo e seus diretórios pais se os mesmos não existem
		try {
			if(!this.file.exists()){
				File parentfile = this.file.getParentFile();
				if(parentfile != null && !parentfile.exists()){
					parentfile.mkdirs();
				}
				this.file.createNewFile();
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//GETTERS E SETTERS
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}	

	//MÉTODOS DE MANIPULAÇÃO DE ARQUIVO
	public boolean saveObject(Object object){
		
		boolean saved = false;
		if (file == null || object == null){
			return saved; 
		}
		else {
			FileOutputStream fileOutStream;
			ObjectOutputStream objOutStream;
			try {
				fileOutStream = new FileOutputStream(file);
				objOutStream = new ObjectOutputStream(fileOutStream);
				objOutStream.writeObject(object);
				saved = true;
				objOutStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			return saved;
		}		
	}
	
	public Object readObject(){

		Object object = null;
		FileInputStream fileInStream;
		ObjectInputStream objInStream;
		try {
			fileInStream = new FileInputStream(file);
			objInStream = new ObjectInputStream(fileInStream);
			object = objInStream.readObject();
			objInStream.close();			
		} catch (Exception e) {
			return null;
		} 
		return object;
	}
}