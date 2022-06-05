package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Simulatore {
	
	//parametri della simulazione
	private int N; 
	private int x;
	
	//output della simulazione
	private double numReporterMedio;
	private int numPartiteCritiche;
	
	//modello del mondo
	private Graph<Team, DefaultWeightedEdge> grafo;
	Map<Integer,TeamReporter> mappaTeams;
	List<Vicino> squadreMigliori;
	List<Vicino> squadrePeggiori;
	
	//coda degli eventi
	List<Match> partite;
	
	public Simulatore(Graph<Team, DefaultWeightedEdge> grafo) {
		this.grafo = grafo;
	}
	
	public void init(int N, int x,List<Team> squadre, List<Match> matches) {
		this.N = N;
		this.x = x;
		this.numReporterMedio=0;
		this.numPartiteCritiche=0;
		
		//creo le squadre assegnando i reporter
		this.mappaTeams = new HashMap<Integer, TeamReporter>();
		for(Team t : squadre) {
			TeamReporter tr = new TeamReporter(t, this.N);
			this.mappaTeams.put(t.getTeamID(), tr);
		}
		
		//creo le partite e le ordino per data e ora
		this.partite = new ArrayList<Match>(matches);
		
		Collections.sort(this.partite, new Comparator<Match>() {

			@Override
			public int compare(Match o1, Match o2) {
				return o1.date.compareTo(o2.date);
			}
		});
		
	}
	
	public void run() {
		
		for(Match match : this.partite) {
			processaPartita(match);
		}
		
	}

	private void processaPartita(Match match) {
		
		int numReporterCasa = this.mappaTeams.get(match.teamHomeID).getNumReporter();
		int numReporterTrasferta = this.mappaTeams.get(match.teamAwayID).getNumReporter();
		
		this.numReporterMedio += numReporterCasa+numReporterTrasferta;
		if(numReporterCasa+numReporterTrasferta<this.x) {
			this.numPartiteCritiche++;
		}
		
		TeamReporter vincente=null;
		TeamReporter perdente=null;
		
		if(match.resultOfTeamHome==1) {
			vincente = this.mappaTeams.get(match.teamHomeID);
			perdente = this.mappaTeams.get(match.teamAwayID);
		} else if (match.resultOfTeamHome==-1) {
			perdente = this.mappaTeams.get(match.teamHomeID);
			vincente = this.mappaTeams.get(match.teamAwayID);
		}
		// se c'è un pareggio, vincente e perdente rimangono a null
		
		if(vincente!=null && perdente!=null) { //se non è pareggio
			
			//devo promuovere un reporter della squadra vincente??
			int random = ((int)Math.random()*100) +1;
			if(random<=50) { //promuovo
				
				if(vincente.getNumReporter()>0) {
					ArrayList<TeamReporter> migliori = new ArrayList<TeamReporter>();
					this.doClassifica(vincente.getTeam());
					for(Vicino v : this.squadreMigliori) {
						TeamReporter tr = mappaTeams.get(v.getTeam().teamID);
						migliori.add(tr);
					}
					
					TeamReporter scelta = this.scegliACaso(migliori);
					if(scelta!=null) {
						vincente.decrementaNumReporter(1);
						scelta.incrementaNumReporter(1);
					}
				}
			}
			
			//devo bocciare dei reporter della squadra perdente??
			random = ((int)Math.random()*100) +1;
			if(random<=20) { //boocio, se ne ha
				int numReporterPerdente = perdente.getNumReporter();
				if(numReporterPerdente>0) {
					
					ArrayList<TeamReporter> peggiori = new ArrayList<TeamReporter>();
					this.doClassifica(perdente.getTeam());
					for(Vicino v : this.squadrePeggiori) {
						TeamReporter tr = mappaTeams.get(v.getTeam().teamID);
						peggiori.add(tr);
					}
					
					TeamReporter scelta = this.scegliACaso(peggiori);
					if(scelta!=null) {
						random = ((int) Math.random()*numReporterPerdente)+1;
						perdente.decrementaNumReporter(random);
						scelta.incrementaNumReporter(random);
					}
					
				}
				
			}
			
		}
		
		
		
	}
	
	private TeamReporter scegliACaso(ArrayList<TeamReporter> lista) {
		
		if(lista.size()==0) return null;
		
		int random = (int) Math.random()*lista.size();
		
		return lista.get(random);
	}
	
	public void doClassifica(Team team) {
		this.squadreMigliori = new ArrayList<Vicino>();
		this.squadrePeggiori = new ArrayList<Vicino>();
		
		for(DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(team)) {
			Vicino v = new Vicino(Graphs.getOppositeVertex(this.grafo, e, team), (int)this.grafo.getEdgeWeight(e));
			this.squadrePeggiori.add(v);
		}
		Collections.sort(this.squadrePeggiori);
		
		for(DefaultWeightedEdge e : this.grafo.incomingEdgesOf(team)) {
			Vicino v = new Vicino(Graphs.getOppositeVertex(this.grafo, e, team), (int)this.grafo.getEdgeWeight(e));
			this.squadreMigliori.add(v);
		}
		Collections.sort(this.squadreMigliori);
		
	}

	public double getNumReporterMedio() {
		double numPartiteTotali = this.partite.size();
		
		return this.numReporterMedio/numPartiteTotali;
	}

	public int getNumPartiteCritiche() {
		return numPartiteCritiche;
	}
	

}
