package me.skiincraft.discord.ousu;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import me.skiincraft.discord.ousu.console.JComponentOutputStream;
import me.skiincraft.discord.ousu.console.JComponentOutputStream.JComponentHandler;

public class ApplicationUtils {

	public static void consoleapp(JFrame myPanel) {
		// https://stackoverflow.com/questions/342990/create-java-console-inside-a-gui-panel
		JLabel console = new JLabel();
		JComponentOutputStream consoleOutputStream = new JComponentOutputStream(console, new JComponentHandler() {
			private StringBuilder sb = new StringBuilder();

			@Override
			public void setText(JComponent swingComponent, String text) {
				sb.delete(0, sb.length());
				append(swingComponent, text);
			}

			@Override
			public void replaceRange(JComponent swingComponent, String text, int start, int end) {
				sb.replace(start, end, text);
				redrawTextOf(swingComponent);
			}

			@Override
			public void append(JComponent swingComponent, String text) {
				sb.append(text);
				redrawTextOf(swingComponent);
			}

			private void redrawTextOf(JComponent swingComponent) {
				((JLabel) swingComponent).setText("<html><pre>" + sb.toString() + "</pre></html>");
			}
		});

		PrintStream con = new PrintStream(consoleOutputStream);
		System.setOut(con);
		System.setErr(con);

		// Optional: add a scrollpane around the console for having scrolling bars
		JScrollPane sp = new JScrollPane( //
				console, //
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, //
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED //
		);
		myPanel.add(sp);
	}

	public static JFrame frame;

	public static void openconsole() {
		frame = new JFrame();
		frame.setTitle("[Bot] - Discord Bot [JDA]");
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		JMenu mnFunes = new JMenu("Funções");
		menuBar.add(mnFunes);

		JMenuItem mntmFechar = new JMenuItem("Fechar");
		mntmFechar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		mnFunes.add(mntmFechar);

		JMenuItem mntmReiniciar = new JMenuItem("Reiniciar");
		mntmReiniciar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Thread.sleep(1000);
					ApplicationUtils.restartApplication(OusuBot.arguments);
				} catch (URISyntaxException | IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		mnFunes.add(mntmReiniciar);

		JSeparator separator = new JSeparator();
		mnFunes.add(separator);

		JMenuItem mntmComando = new JMenuItem("SpyMode");
		mnFunes.add(mntmComando);

		consoleapp(frame);

		frame.setIconImage(Toolkit.getDefaultToolkit()
				.getImage(OusuBot.class.getResource("/me/skiincraft/discord/ousu/ousu-logo128x.png")));
		frame.pack();

		frame.setVisible(true);
		frame.setSize(500, 400);
	}

	public static void restartApplication(String[] args) throws URISyntaxException, IOException {
		StringBuilder cmd = new StringBuilder();
		cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");
		for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
			cmd.append(jvmArg + " ");
		}
		cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
		cmd.append(OusuBot.class.getName()).append(" ");
		for (String arg : args) {
			cmd.append(arg).append(" ");
		}
		Runtime.getRuntime().exec(cmd.toString());
		System.exit(0);
	}
}
