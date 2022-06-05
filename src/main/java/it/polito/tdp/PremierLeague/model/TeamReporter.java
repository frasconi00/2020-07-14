package it.polito.tdp.PremierLeague.model;

public class TeamReporter {
	
	private Team team;
	private int numReporter;
	
	public TeamReporter(Team team, int numReporter) {
		this.team = team;
		this.numReporter = numReporter;
	}

	public Team getTeam() {
		return team;
	}

	public int getNumReporter() {
		return numReporter;
	}
	
	public void incrementaNumReporter(int num) {
		this.numReporter = this.numReporter + num;
	}
	
	public void decrementaNumReporter(int num) {
		this.numReporter = this.numReporter - num;
	}

}
