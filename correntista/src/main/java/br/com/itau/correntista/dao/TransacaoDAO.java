package br.com.itau.correntista.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.itau.correntista.models.Transacao;

public class TransacaoDAO {
	
	private Connection conexao;

	public TransacaoDAO(Connection conexao) {
		this.conexao = conexao;
	}

	public int adiciona(Transacao transacao) {
		try {
			
			PreparedStatement ps = this.conexao
					.prepareStatement("insert into transacao(id_correntista, fluxo, saldo_anterior, saldo_atualizado) value (?, ?, ?, ?)");
			ps.setLong(1, transacao.getCorrentista().getId());
			ps.setDouble(2, transacao.getFluxo());
			ps.setDouble(3, transacao.getSaldoAnterior());
			ps.setDouble(4, transacao.getSaldoAtualizado());
			int rows = ps.executeUpdate();
			ps.close();
			conexao.close();
			return rows;
		} catch (SQLException e) {
			System.err.println("Erro ao adicionar a transacao --> "+e.getMessage());
		}
		return 0;
	}
	
	public Double getUltimoRegistroCorrentista(Long idCorrentista) {
		try (PreparedStatement ps = this.conexao.prepareStatement("select saldo_atualizado  from transacao  where id = (select max(id)  from transacao where id_correntista = ?)")) {
			ps.setLong(1, idCorrentista);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				return rs.getDouble("saldo_atualizado");
			}
			conexao.close();
		} catch (SQLException e) {
			System.err.println("Erro ao retornar o saldo do correntista de id --> " + idCorrentista + "---> " + e.getMessage());
		}
		return 0d;
	}
	
	public List<Transacao> buscaPorCorrentista(Long idCorrentista) {
		List<Transacao> transacoes = new ArrayList<Transacao>();
		try (PreparedStatement ps = this.conexao.prepareStatement("select fluxo, saldo_anterior, saldo_atualizado, tipo, creation_time  from transacao  where id_correntista = ? order by id")) {
			ps.setLong(1, idCorrentista);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				transacoes.add(new Transacao(rs.getDouble("fluxo"), 
						rs.getDouble("saldo_anterior"), 
						rs.getDouble("saldo_atualizado"), 
						rs.getString("tipo"), 
						rs.getDate("creation_time")));
			}
			conexao.close();
			return transacoes;
		} catch (SQLException e) {
			System.err.println("Erro ao retornar o saldo do correntista de id --> " + idCorrentista + "---> " + e.getMessage());
		}
		return transacoes;
	}
	
	
}
