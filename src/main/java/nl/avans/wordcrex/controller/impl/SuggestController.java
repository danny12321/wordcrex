package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.SuggestView;

import java.util.Map;
import java.util.function.Function;

public class SuggestController extends Controller<User> {

	public SuggestController(Main main, Function<User, User> fn)
	{
		super(main, fn);
	}

	public Map<String, String> getWords() {
		return Map.of(
				"takelen", "Accepted",
				"wielrennen", "Pending",
				"yeet", "Denied",
				"computer", "Accepted",
				"Aqua", "Pending",
				"Kappa", "Denied"
		);
	}



	@Override
	public View<? extends Controller<User>> createView()
	{
		return new SuggestView(this);
	}
}
