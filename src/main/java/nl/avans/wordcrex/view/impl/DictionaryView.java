package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.DictionaryController;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ScrollbarWidget;
import nl.avans.wordcrex.controller.impl.*;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DictionaryView extends View<DictionaryController>
{
	private Main main;
	private String dictionary = "";
	private boolean invalid;

	private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll);
	private int scroll;

	public DictionaryView(DictionaryController controller) { super(controller); }

	@Override
	public void draw(Graphics2D g) {
		if (this.invalid) {
			g.setColor(Colors.DARK_RED);
			g.fillRect(64, 360, 184, 32);
			g.setColor(Color.WHITE);
			StringUtil.drawCenteredString(g, 64, 360, 184, 32, "Word already known");
		}

		var metrics = g.getFontMetrics(g.getFont());
		var index = new AtomicInteger();
		var dictionaries = this.controller.getDictionaries();

		dictionaries.forEach((key,value) -> {
			var offset = index.getAndIncrement() * 88 - this.scroll;

			g.setColor(Color.WHITE);
			g.drawString(key, Main.TASKBAR_SIZE, 100 + offset);
			g.drawString(value, Main.TASKBAR_SIZE + 100, 100 + offset);
			new ButtonWidget("Suggest", Main.FRAME_SIZE - Main.TASKBAR_SIZE * 2,100 + offset,184,48, () -> this.Suggest(key));

			if (index.get() < dictionaries.size()) {
				g.setColor(Colors.DARKERER_BLUE);
				g.fillRect(Main.TASKBAR_SIZE * 2 + 42, 134 + offset, 268, 4);
			}
		});

		this.scrollbar.setHeight(dictionaries.size() * 88 + 128);
	}

	@Override
	public void update()
	{}
/*
	@Override
	public java.util.List<Widget> getChildren()
	{
		return List.of(
				new InputWidget("Word", 64, 42, 384, 48, (value) -> this.dictionary = value),
				new ButtonWidget("Suggest", 64,100,184,48, this::Suggest)
		);
	}
*/

	private void Suggest(String DictKey)
	{
		String dict = DictKey;
		System.out.println(dict);

		//() -> this.main.openController(SuggestController.class);
	}

	@Override
	public List<Widget> getChildren() {
		return List.of(
				this.scrollbar
		);
	}
}
