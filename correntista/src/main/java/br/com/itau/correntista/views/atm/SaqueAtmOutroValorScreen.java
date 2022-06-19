package br.com.itau.correntista.views.atm;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import br.com.itau.correntista.models.Correntista;
import br.com.itau.correntista.models.Transacao;
import br.com.itau.correntista.repositories.ITransacaoRepository;
import br.com.itau.correntista.repositories.impl.TransacaoRepository;
import br.com.itau.correntista.store.CorrentistaLogado;

public class SaqueAtmOutroValorScreen extends JFrame {

	private JPanel contentPane;
	private JFormattedTextField txtValorSaque;
	private JLabel lblConta;
	private JLabel lblAgencia;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SaqueAtmOutroValorScreen frame = new SaqueAtmOutroValorScreen();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SaqueAtmOutroValorScreen() {
		setTitle("DEPÓSITO - ICARROS");
		setResizable(false);
		setBounds(450, 250, 425, 386);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnSacar = new JButton("SACAR");
		btnSacar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Double valorSaque = Double.parseDouble(txtValorSaque.getText().replace(".", "").replace(",", "."));
					if(valorSaque <= 0) {
						JOptionPane.showMessageDialog(null, "Apenas valores maiores que zero são aceitos para efetuar essa operação!", "Erro: saque!", JOptionPane.ERROR_MESSAGE);
						return;
					}
					fazerSaque(valorSaque);
					irTelaPrincipal();
					setVisible(false);
					dispose();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Erro inesperado ao realizar o saque --> " + e1.getMessage(), "Erro: saque!", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		btnSacar.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnSacar.setBounds(142, 225, 133, 32);
		contentPane.add(btnSacar);
		
		JLabel lblSaque = new JLabel("SAQUE");
		lblSaque.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblSaque.setBounds(173, 91, 80, 23);
		contentPane.add(lblSaque);

		DecimalFormat dFormat = new DecimalFormat("#,##0.00");
	    NumberFormatter formatter = new NumberFormatter(dFormat);
	    formatter.setFormat(dFormat);
	    formatter.setAllowsInvalid(false);
	    
		txtValorSaque = new JFormattedTextField(formatter);
		txtValorSaque.setBounds(142, 153, 133, 32);
		contentPane.add(txtValorSaque);
		txtValorSaque.setColumns(10);
		
		lblAgencia = new JLabel("AGÊNCA: null");
		lblAgencia.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblAgencia.setBounds(149, 22, 118, 14);
		contentPane.add(lblAgencia);
		
		lblConta = new JLabel("CONTA: null");
		lblConta.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblConta.setBounds(149, 47, 118, 14);
		contentPane.add(lblConta);
		
		JButton btnVoltar = new JButton("MENU");
		btnVoltar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				irTelaPrincipal();
				setVisible(false);
				dispose();
			}
		});
		btnVoltar.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnVoltar.setBounds(142, 275, 133, 32);
		contentPane.add(btnVoltar);
		carregaInformacoesUsuarioLogado();
	}
	public void carregaInformacoesUsuarioLogado() {
		lblAgencia.setText("AGÊNCA: " + CorrentistaLogado.getInstance().getAgencia());
		lblConta.setText("CONTA: " + CorrentistaLogado.getInstance().getConta());
	}
	private void fazerSaque(Double valor) {
		ITransacaoRepository repository = new TransacaoRepository();
		Correntista correntista = new Correntista();
		correntista.setId(CorrentistaLogado.getInstance().getId());
		Double saldoAnterior = repository.buscaSaldoCorrentista(correntista.getId());
		if(valor > saldoAnterior) {
			JOptionPane.showMessageDialog(null, "Saldo insuficiente para esta transação!", "Saldo insuficiente", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Double valorSaque = valor;
		int rows = repository.gravaTransacao(new Transacao(valorSaque, saldoAnterior, saldoAnterior - valorSaque, correntista));
		if(rows > 0 ) {
			JOptionPane.showMessageDialog(null, "Saque realizado com sucesso!", "Saque: sucesso!", JOptionPane.INFORMATION_MESSAGE);
			PrincipalATM principalATM = new PrincipalATM();
			principalATM.setVisible(true);
			setVisible(false);
			dispose();
		} else {
			JOptionPane.showMessageDialog(null, "Erro ao registrar o saque", "Saque: erro!", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void irTelaPrincipal() {
		PrincipalATM principal = new PrincipalATM();
		principal.setVisible(true);
	}
	protected JLabel getLblConta() {
		return lblConta;
	}
	protected JLabel getLblAgencia() {
		return lblAgencia;
	}
}
