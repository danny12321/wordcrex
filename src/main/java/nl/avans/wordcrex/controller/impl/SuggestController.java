package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.model.Word;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.SuggestView;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SuggestController extends Controller<User> {
	private int page;
	private Map<String, List<Word>> words;
	private String languageCode;

	public SuggestController(Main main, Function<User, User> fn)
	{
		super(main, fn);
		this.setPage(0);
	}

	@Override
	public View<? extends Controller<User>> createView()
	{
		return new SuggestView(this);
	}

	public void addWord(String word)
	{
		this.getModel().submitNewWord(word, languageCode);
		this.setPage(this.page);
	}

	public void setLanguage(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getLanguage()
	{
		return languageCode;
	}

	public void setPage(int page) {
		this.page = page;
		this.words = this.getModel().getSuggestedWords(this.page);
	}

	public int getPage()
	{
		return page;
	}

	public Map<String, List<Word>> getWords()
	{
		return words;
	}
}
