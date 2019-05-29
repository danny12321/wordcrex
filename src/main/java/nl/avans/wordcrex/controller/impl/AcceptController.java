package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.model.Word;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.AcceptView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AcceptController extends Controller<User>
{
	private List<Word> words;

	public AcceptController(Main main, Function<User, User> fn)
	{
		super(main, fn);
		this.words = this.getModel().getPendingWords();
	}

	@Override
	public View<? extends Controller<User>> createView()
	{
		return new AcceptView(this);
	}

	public List<Word> getWords()
	{
		return words;
	}

}
