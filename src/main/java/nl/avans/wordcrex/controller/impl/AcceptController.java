package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.AcceptView;

import java.util.function.Function;

public class AcceptController extends Controller<User>
{
	public AcceptController(Main main, Function<User, User> fn)
	{
		super(main, fn);
	}

	@Override
	public View<? extends Controller<User>> createView()
	{
		return new AcceptView(this);
	}

}
