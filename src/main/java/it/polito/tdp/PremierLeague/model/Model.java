package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private Map<Integer,Team> idMapTeam;
	
	private List<Vicino> squadreMigliori;
	private List<Vicino> squadrePeggiori;
	
	private Graph<Team, DefaultWeightedEdge> grafo;
	
	public Model() {
		this.dao = new PremierLeagueDAO();
		this.idMapTeam = new HashMap<Integer, Team>();
		this.dao.listAllTeams(idMapTeam);
	}
	
	public void creaGrafo() {
		
		//creo il grafo
		this.grafo = new SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//aggiungo i vertici
		Graphs.addAllVertices(this.grafo, this.idMapTeam.values());
		
//		System.out.println("#vertici:"+this.grafo.vertexSet().size());
		
		//calcolo il punteggio di ogni squadra a fine stagione
		for(Match m : this.dao.listAllMatches()) {
			
			Team t1 = this.idMapTeam.get(m.teamHomeID);
			Team t2 = this.idMapTeam.get(m.teamAwayID);
			
			if(m.resultOfTeamHome==1) {
				t1.punteggioFinale+=3;
			} else if(m.resultOfTeamHome==-1) {
				t2.punteggioFinale+=3;
			} else {
				//pareggio
				t1.punteggioFinale++;
				t2.punteggioFinale++;
			}
			
		}
		
		//ora ho i punteggi
		
		//aggiungo gli archi
		for(Team t1 : this.grafo.vertexSet()) {
			for(Team t2 : this.grafo.vertexSet()) {
				
				if(t1.teamID<t2.teamID) {
					
					int differenzaPunti = t1.punteggioFinale - t2.punteggioFinale;
					if(differenzaPunti>0) {
						Graphs.addEdgeWithVertices(this.grafo, t1, t2, differenzaPunti);
					} else if(differenzaPunti<0) {
						Graphs.addEdgeWithVertices(this.grafo, t2, t1, (-differenzaPunti));
					}
					
				}
				
			}
		}
		
//		System.out.println("#archi:"+this.grafo.edgeSet().size());
		
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public boolean grafoCreato() {
		if(this.grafo == null)
			return false;
		else
			return true;
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

	public List<Vicino> getSquadreMigliori() {
		return squadreMigliori;
	}

	public List<Vicino> getSquadrePeggiori() {
		return squadrePeggiori;
	}

	public Map<Integer, Team> getIdMapTeam() {
		return idMapTeam;
	}
	
	public void simula(int N, int x) {
		
		Simulatore sim = new Simulatore(this.grafo);
		
		List<Team> lista = new ArrayList<Team>(this.grafo.vertexSet());
		
		sim.init(N, x, lista, this.dao.listAllMatches());
		
		sim.run();
		
		System.out.println("num reporter medio: "+sim.getNumReporterMedio());
		System.out.println("soglia: "+x);
		System.out.println("num partite sotto la soglia: "+sim.getNumPartiteCritiche());
		
	}
	
	
}
