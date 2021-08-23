package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import entita.LoadBalancerImpl;
import entita.ServerImpl;
import strategieLoadBalancer.MenoCarico;
import strategieLoadBalancer.PrimoServer;
import strategieLoadBalancer.RoundRobinRichieste;
import strategieLoadBalancer.RoundRobinTime;
import strategieLoadBalancer.Strategia;

public class LoadBalancerGUI extends JFrame implements Serializable {
	private static final long serialVersionUID = 1L;

	private JPanel panel_ViewSystem = new JPanel();
	private JPanel panel_Strategy = new JPanel();
	private JPanel panel_Log;
	private JMenu mnScegli = new JMenu("Scegli server..");
	private JMenuBar menuBar = new JMenuBar();
	private ArrayList<String> listaLog = new ArrayList<String>();

	private ImageIcon imageServer = new ImageIcon(System.getProperties().getProperty("user.dir") + "/res/server.png");
	private String[] strategies = { "Primo Server", "Meno Carico", "Round Robin Richieste", "Round Robin Time Slice" };
	private Strategia[] listaStrategie = { new PrimoServer(), new MenoCarico(), new RoundRobinRichieste(),
			new RoundRobinTime() };
	private LoadBalancerImpl lb;
	private ArrayList<JLabel> listaLabel = new ArrayList<JLabel>();

	public LoadBalancerGUI(LoadBalancerImpl lb) {
		setTitle("Load Balancer Window");
		setSize(780, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.lb = lb;

		panel_ViewSystem.setLayout(new GridLayout(6, 4, 5, 5));
		JScrollPane jsp = new JScrollPane(panel_ViewSystem);
		getContentPane().add(jsp, BorderLayout.CENTER);

		panel_Strategy.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		aggiungiPannelloStrategie();
		JScrollPane jsp2 = new JScrollPane(panel_Strategy);
		getContentPane().add(jsp2, BorderLayout.SOUTH);

		JPanel panel_Control = new JPanel();
		getContentPane().add(panel_Control, BorderLayout.EAST);
		panel_Control.setLayout(new BorderLayout(0, 0));

		JPanel panel_CreaServer = new JPanel();
		panel_Control.add(panel_CreaServer, BorderLayout.NORTH);
		panel_CreaServer.setLayout(new BoxLayout(panel_CreaServer, BoxLayout.Y_AXIS));

		JPanel panel_1 = new JPanel();
		panel_CreaServer.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));

		JButton btnCreaNuovoServer = new JButton("Crea nuovo Server");
		btnCreaNuovoServer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					try {
						new ServerImpl();
					} catch (RemoteException e1) {
						e1.printStackTrace();
						System.out.println("Server non creato.");
					}
					// new AddServerGUI(e);
					LoadBalancerGUI.this.panel_ViewSystem.revalidate();
					LoadBalancerGUI.this.panel_ViewSystem.repaint();
				}
			}
		});
		panel_1.add(btnCreaNuovoServer);

		JPanel panel_Rimuovi = new JPanel();
		panel_Control.add(panel_Rimuovi, BorderLayout.SOUTH);
		panel_Rimuovi.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		addListaRimuovibili();

		panel_Rimuovi.add(menuBar);

		menuBar.add(mnScegli);
		addListaRimuovibili();
		JButton btnRimuovi = new JButton("Rimuovi");
		panel_Rimuovi.add(btnRimuovi);
		btnRimuovi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					String s = mnScegli.getText();
					if (!s.equals("Scegli server..")) {
//						System.out.println("RIMOSSOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
						String sid = s.substring(7);
						lb.rimuoviServer(Integer.parseInt(sid));
					}
				}
			}
		});

		JPanel panel_LogView = new JPanel();
		panel_Control.add(panel_LogView);
		panel_LogView.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel_LogView.add(panel, BorderLayout.NORTH);

		JLabel lblLogRichieste = new JLabel("LOG RICHIESTE");
		panel.add(lblLogRichieste);

		panel_Log = new JPanel();
		JScrollPane jsp3 = new JScrollPane(panel_Log);
		panel_Log.setLayout(new BoxLayout(panel_Log, BoxLayout.Y_AXIS));
		panel_LogView.add(jsp3);

		revalidate();
		repaint();
		setVisible(true);
	}

	private void addListaRimuovibili() {
		menuBar.remove(mnScegli);
		menuBar.revalidate();
		menuBar.repaint();
		ButtonGroup bg2 = new ButtonGroup();
		mnScegli = new JMenu("Scegli server..");
		for (int i = 0; i < listaLabel.size(); i++) {
			JRadioButtonMenuItem buttonServer = new JRadioButtonMenuItem(listaLabel.get(i).getText());
			buttonServer.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					mnScegli.setText(buttonServer.getText());
				}
			});
			bg2.add(buttonServer);
			mnScegli.add(buttonServer);
		}
		menuBar.add(mnScegli);
		menuBar.revalidate();
		menuBar.repaint();
	}

	private void aggiungiPannelloStrategie() {
		ButtonGroup bg = new ButtonGroup();
		for (int i = 0; i < strategies.length; i++) {
			JRadioButton buttonStrat = new JRadioButton(strategies[i]);
			bg.add(buttonStrat);
			buttonStrat.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						JRadioButton comp = (JRadioButton) e.getComponent();
						String strategiaScelta = comp.getText();
						for (int j = 0; j < strategies.length; j++) {
							if (strategiaScelta.equals(strategies[j])) {
								try {
									lb.cambiaStrategia(listaStrategie[j]);
								} catch (RemoteException e1) {
									e1.printStackTrace();
								}
								break;
							}
						}
					}
				}
			});
			panel_Strategy.add(buttonStrat);
		}
	}

	public void changeList(int sid, ArrayList<Integer> listaSIDRimossi) {
		for (Component c : panel_ViewSystem.getComponents()) {
			panel_ViewSystem.remove(c);
		}
		listaLabel = new ArrayList<JLabel>();
		for (int i = 1; i < sid; i++) {
			if (!listaSIDRimossi.contains(i)) {
				JLabel tagServer = new JLabel("Server " + i);
				listaLabel.add(tagServer);
				tagServer.setIcon(imageServer);
				panel_ViewSystem.add(tagServer);
			}
		}
		addListaRimuovibili();
		panel_ViewSystem.revalidate();
		panel_ViewSystem.repaint();
	}

	public void selectedServer(int ssid) {
		for (int i = 0; i < listaLabel.size(); i++) {
			String s = listaLabel.get(i).getText();
			if (s.equals("Server " + ssid)) {
				listaLabel.get(i).setBorder(new LineBorder(Color.GREEN));
				listaLabel.get(i).revalidate();
				listaLabel.get(i).repaint();
			} else {
				listaLabel.get(i).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
				listaLabel.get(i).revalidate();
				listaLabel.get(i).repaint();
			}
		}
	}

	public synchronized void addLog(String s) {
		for (Component c : panel_Log.getComponents()) {
			panel_Log.remove(c);
		}
		listaLog.add(s);
		for (String st : listaLog) {
			panel_Log.add(new JLabel(st));
		}
		panel_Log.revalidate();
		panel_Log.repaint();
	}
}