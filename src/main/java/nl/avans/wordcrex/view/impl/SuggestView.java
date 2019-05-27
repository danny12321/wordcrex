package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.SuggestController;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ScrollbarWidget;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class SuggestView extends View<SuggestController>
{
	private String word = "";
	private boolean invalid;

	private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll);
	private int scroll;

	public SuggestView(SuggestController controller)
	{
		super(controller);
	}

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
		var words = this.controller.getWords();

		words.forEach((key,value) -> {
			var offset = index.getAndIncrement() * 88 - this.scroll;

			g.setColor(Color.WHITE);
			g.drawString(key, Main.TASKBAR_SIZE, 202 + offset);
			g.drawString(value, Main.FRAME_SIZE - Main.TASKBAR_SIZE * 2 - metrics.stringWidth(value), 202 + offset);

			if (index.get() < words.size()) {
				g.setColor(Colors.DARKERER_BLUE);
				g.fillRect(Main.TASKBAR_SIZE * 2 + 42, 236 + offset, 268, 4);
			}
		});

		this.scrollbar.setHeight(words.size() * 88 + 128);
	}

	@Override
	public void update()
	{}

	@Override
	public List<Widget> getChildren()
	{
		return List.of(
			new InputWidget("Word", 64, 42, 384, 48, (value) -> this.word = value),
			new ButtonWidget("Suggest", 64,100,184,48, this::Suggest)
		);
	}

	private void Suggest()
	{
		//this.invalid = !
	}
}