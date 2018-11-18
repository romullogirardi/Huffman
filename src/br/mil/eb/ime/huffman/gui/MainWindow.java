package br.mil.eb.ime.huffman.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import br.mil.eb.ime.huffman.model.HuffmanManager;

public class MainWindow {

	//ELEMENTOS DA GUI
	private final Font BASE_FONT = new Font("Helvetica", Font.BOLD, 20);
	private JFrame frame;
	private JButton compressButton;
	private JButton decompressButton;
	
	//MÉTODO PRINCIPAL
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//CONSTRUTOR
	public MainWindow() {
		initialize();
	}

	//INICIALIZAÇÃO DA GUI
	private void initialize() {
		
		//Inicializando a janela
		frame = new JFrame();
		frame.setTitle("Compressão/Descompressão de HUFFMAN");
		frame.setSize(750, 250);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Inicializando o painel principal
		frame.getContentPane().setLayout(new BorderLayout());
		
		//Adicionando o texto ao painel principal
		JLabel question = new JLabel("Que ação deseja executar?");
		question.setForeground(Color.BLACK);
		question.setFont(BASE_FONT);
		question.setHorizontalAlignment(SwingConstants.CENTER);
        frame.getContentPane().add(question, BorderLayout.CENTER);

        //Adicionando o painel de botões ao painel principal
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2));
        frame.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        
		//Adicionando o botão de compressão ao painel de botões
		compressButton = new JButton("COMPRIMIR");
		compressButton.setPreferredSize(new Dimension(0, 100));
		compressButton.setForeground(Color.BLACK);
		compressButton.setFont(BASE_FONT);
		buttonsPanel.add(compressButton);
		compressButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickCompressButton();
			}
		});

		//Adicionando o botão de descompressão avaliação ao painel de botões
		decompressButton = new JButton("DESCOMPRIMIR");
		decompressButton.setPreferredSize(new Dimension(0, 100));
		decompressButton.setForeground(Color.BLACK);
		decompressButton.setFont(BASE_FONT);
		buttonsPanel.add(decompressButton);
		decompressButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickDecompressButton();
			}
		});
	}
	
	//MÉTODOS DE CLIQUE NOS BOTÕES	
	private void onClickCompressButton() {
		
		//Abrir o gerenciador de arquivos para selecionar o caminho do arquivo a ser comprimido
		final JFileChooser sourceFileChooser = new JFileChooser();
		sourceFileChooser.setDialogTitle("Selecione o arquivo a ser comprimido");
		sourceFileChooser.setApproveButtonText("Carregar");
		int sourceReturnValue = sourceFileChooser.showOpenDialog(frame.getContentPane());
        if (sourceReturnValue == JFileChooser.APPROVE_OPTION) {
            
        	//Guardar referência para o arquivo fonte .txt
        	File sourceTxtFile = sourceFileChooser.getSelectedFile();
        	
			//Abrir o gerenciador de arquivos para selecionar o caminho do arquivo de destino
        	@SuppressWarnings("serial")
        	final JFileChooser destinationFileChooser = new JFileChooser(){
        	    
        		/*
        		 * Sobrescrita do método de aprovação para verificar se o usuário 
        		 * deseja sobrescrever o arquivo, caso o arquivo selecionado já exista
        		 */
        		@Override
        	    public void approveSelection() {
        	        
        			File selectedFile = getSelectedFile();
        	        if(selectedFile.exists()) {
        	            int result = JOptionPane.showConfirmDialog(this, 
        	            	"O arquivo " + selectedFile.getName() + " já existe. Deseja sobrescrevê-lo?",
        	            	"Arquivo já existente",
        	            	JOptionPane.YES_NO_OPTION,
        	            	JOptionPane.PLAIN_MESSAGE);
        	            switch(result){
        	                case JOptionPane.YES_OPTION:
        	                    super.approveSelection();
        	                    return;
        	                case JOptionPane.NO_OPTION:
        	                    return;
        	                case JOptionPane.CLOSED_OPTION:
        	                    return;
        	            }
        	        }
        	        super.approveSelection();
        	    }        
        	};
			destinationFileChooser.setDialogTitle("Selecione o arquivo .zip de destino da compressão");
			destinationFileChooser.setApproveButtonText("Salvar");
			destinationFileChooser.setFileFilter(new FileNameExtensionFilter("Extensão .zip", "zip"));
			int destinationReturnValue = destinationFileChooser.showOpenDialog(frame.getContentPane());
	        if (destinationReturnValue == JFileChooser.APPROVE_OPTION) {
	            
	        	//Guardar referência para o arquivo destino .zip
	        	File destinationZipFile = destinationFileChooser.getSelectedFile();
	        	
	        	//Comprimir
	        	String message = HuffmanManager.getInstance().compress(sourceTxtFile, destinationZipFile);
	        	
	        	//Retornar feedback sobre a compressão
	        	if(message == null)
	        		message = "Ocorreu uma falha na compressão";
				JLabel messageLabel = new JLabel(message);
				messageLabel.setForeground(Color.BLACK);
				messageLabel.setFont(BASE_FONT);
				messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
				JOptionPane.showMessageDialog(frame.getContentPane(), message, "Alerta", JOptionPane.PLAIN_MESSAGE);
				
				//Fechar aplicação
				frame.dispose();
	        }
		}
	}
	
	private void onClickDecompressButton() {
		
		//Abrir o gerenciador de arquivos para selecionar o caminho do arquivo a ser descomprimido
		final JFileChooser sourceFileChooser = new JFileChooser();
		sourceFileChooser.setDialogTitle("Selecione o arquivo .zip a ser descomprimido");
		sourceFileChooser.setApproveButtonText("Carregar");
		sourceFileChooser.setFileFilter(new FileNameExtensionFilter("Extensão .zip", "zip"));
		int sourceReturnValue = sourceFileChooser.showOpenDialog(frame.getContentPane());
        if (sourceReturnValue == JFileChooser.APPROVE_OPTION) {
            
        	//Guardar referência para o arquivo fonte .txt
        	File sourceZipFile = sourceFileChooser.getSelectedFile();
        	
			//Abrir o gerenciador de arquivos para selecionar o caminho do arquivo de destino
        	@SuppressWarnings("serial")
        	final JFileChooser destinationFileChooser = new JFileChooser(){
        	    
        		/*
        		 * Sobrescrita do método de aprovação para verificar se o usuário 
        		 * deseja sobrescrever o arquivo, caso o arquivo selecionado já exista
        		 */
        		@Override
        	    public void approveSelection() {
        	        
        			File selectedFile = getSelectedFile();
        	        if(selectedFile.exists()) {
        	            int result = JOptionPane.showConfirmDialog(this, 
        	            	"O arquivo " + selectedFile.getName() + " já existe. Deseja sobrescrevê-lo?",
        	            	"Arquivo já existente",
        	            	JOptionPane.YES_NO_OPTION,
        	            	JOptionPane.PLAIN_MESSAGE);
        	            switch(result){
        	                case JOptionPane.YES_OPTION:
        	                    super.approveSelection();
        	                    return;
        	                case JOptionPane.NO_OPTION:
        	                    return;
        	                case JOptionPane.CLOSED_OPTION:
        	                    return;
        	            }
        	        }
        	        super.approveSelection();
        	    }        
        	};
			destinationFileChooser.setDialogTitle("Selecione o arquivo de destino da descompressão");
			destinationFileChooser.setApproveButtonText("Salvar");
			int destinationReturnValue = destinationFileChooser.showOpenDialog(frame.getContentPane());
	        if (destinationReturnValue == JFileChooser.APPROVE_OPTION) {
	            
	        	//Guardar referência para o arquivo destino .zip
	        	File destinationTxtFile = destinationFileChooser.getSelectedFile();
	        	
	        	//Comprimir
	        	String message;
	        	if(HuffmanManager.getInstance().decompress(sourceZipFile, destinationTxtFile))
	        		message = "DESCOMPRESSÃO REALIZADA COM SUCESSO!";
	        	else
	        		message = "OCORREU UMA FALHA NA DESCOMPRESSÃO!";
	        	
	        	//Retornar feedback sobre a descompressão
				JLabel messageLabel = new JLabel(message);
				messageLabel.setForeground(Color.BLACK);
				messageLabel.setFont(BASE_FONT);
				messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
				JOptionPane.showMessageDialog(frame.getContentPane(), message, "Alerta", JOptionPane.PLAIN_MESSAGE);
				
				//Fechar aplicação
				frame.dispose();
	        }
		}
	}
}