package me.skiincraft.discord.ousu.search;

import java.util.List;
import java.util.Map;

public class JsonReceiver {

	// Coloquei em objetos porque oque me interessa é somente a variavel items
	private Object kind;
	private Object url;
	private Object queries;
	private Object context;
	private Object searchInformation;
	private List<Map<String, Object>> items;// <-

	public Object getKind() {
		return kind;
	}

	public void setKind(Object kind) {
		this.kind = kind;
	}

	public Object getUrl() {
		return url;
	}

	public void setUrl(Object url) {
		this.url = url;
	}

	public Object getQueries() {
		return queries;
	}

	public void setQueries(Object queries) {
		this.queries = queries;
	}

	public Object getContext() {
		return context;
	}

	public void setContext(Object context) {
		this.context = context;
	}

	public Object getSearchInformation() {
		return searchInformation;
	}

	public void setSearchInformation(Object searchInformation) {
		this.searchInformation = searchInformation;
	}

	public List<Map<String, Object>> getItems() {
		return items;
	}

	public void setItems(List<Map<String, Object>> items) {
		this.items = items;
	}

}
