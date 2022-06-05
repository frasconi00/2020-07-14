package it.polito.tdp.PremierLeague.model;

public class Vicino implements Comparable<Vicino>{
	
	private Team team;
	private int pesoArco;
	
	public Vicino(Team team, int pesoArco) {
		this.team = team;
		this.pesoArco = pesoArco;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public int getPesoArco() {
		return pesoArco;
	}

	public void setPesoArco(int pesoArco) {
		this.pesoArco = pesoArco;
	}

	@Override
	public int compareTo(Vicino o) {
		return this.pesoArco-o.pesoArco;
	}
	

}
