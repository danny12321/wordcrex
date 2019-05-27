package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.DictionaryView;

import java.util.Map;
import java.util.function.Function;

public class DictionaryController extends Controller<User>
{
	public DictionaryController(Main main, Function<User, User> fn)
	{
		super(main, fn);
	}

	public Map<String, String> getDictionaries() {
		return Map.of(
				"NL", "Nederlands",
				"EN", "Engels",
				"BE", "België"
		);
	}


	@Override
	public View<? extends Controller<User>> createView()
	{
		return new DictionaryView(this);
	}
}
