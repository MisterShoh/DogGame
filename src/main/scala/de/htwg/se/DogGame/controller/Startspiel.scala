package main.scala.de.htwg.se.DogGame.controller

import com.google.inject.Inject
import main.scala.de.htwg.se.DogGame.model.LauffeldInterfaces
import main.scala.de.htwg.se.DogGame.model.LauffeldInterfaces

class Startspiel @Inject() (var guiBoolean: Boolean) extends StartspielInterface {

  import scala.collection.mutable.ArrayBuffer
  import _root_.main.scala.de.htwg.se.DogGame.model.Spieler
  import _root_.main.scala.de.htwg.se.DogGame.model.Karten
  import _root_.main.scala.de.htwg.se.DogGame.model.Lauffeld
  import _root_.main.scala.de.htwg.se.DogGame.model.Spielbrett
  import _root_.main.scala.de.htwg.se.DogGame.view.Tui
  import _root_.main.scala.de.htwg.se.DogGame.controller.{ Operationlogik => op_log }
  import main.scala.de.htwg.se.DogGame.controller.Benutzerinput
  import org.apache.logging.log4j.{ LogManager, Logger, Level }
  import scala.swing._

  import com.google.inject.Guice
  import main.scala.de.htwg.se.DogGame.DependencyModule
  import _root_.main.scala.de.htwg.se.DogGame.model.LauffeldInterfaces
  val injector = Guice.createInjector(new DependencyModule(guiBoolean))
  var lFInterface = injector.getInstance(classOf[LauffeldInterfaces])
  //var spInterface = injector.getInstance(classOf[Spieler])
  var kInterface = injector.getInstance(classOf[Karten])
  var sbInterface = injector.getInstance(classOf[Spielbrett])

  var tuiIns = Tui()
  val logger: Logger = LogManager.getLogger(this.getClass.getName)

  //var revert7 = false

  // Alle Lauffelder mit spBrett.get_spiel_Lauf_Feld() ersetzt
  override def start_spiel() {
    logger.log(Level.INFO, "start_spiel(): Spiel getartet")
    //var kartenStapel = ArrayBuffer[Int]()
    var kartenStapel = kInterface
    var laufFeld = lFInterface
    var players = ArrayBuffer[Spieler]()
    //val kartFiguren = Array("Kreuz", "Pik", "Herz", "Karo")
    var turn = 1
    var karGrenz = 6
    var spBrett = sbInterface

    // init spieler
    if (guiBoolean) {
      guiIns.frame_comp.textLabel.text_=("Wieviele Spieler spielen?")
    } else {
      println("Wieviele Spieler spielen?")
    }

    var anzSpieler = 0
    var cor = false

    anzSpieler = Benutzerinput(guiBoolean, guiIns).anz_Spieler_waehlen()

    // default- 1 to 4
    addSpieler(players, anzSpieler)

    // init
    for (x <- players) {
      x.setStart();
    }

    var win = -1
    while (win == -1) {

      // kartenstapel auffuellen
      kartenStapel.getKarten().clear()
      for (wert <- 1 to 14) {
        for (anz <- 1 to 4) {
          kartenStapel.setKarte(wert)

        }

      }
      //println(kartenStapel);

      //karten verteilen

      verteieleKarte(players, kartenStapel, karGrenz)

      // karten mit partner tauschen

      tauscheKarte(players, laufFeld)
      // spiel ...
      var rundenEnde = false
      while (!rundenEnde) {
        for (sp <- 0 to players.length - 1) {

          // Alle Felder uebersichtlich ausgeben
          if (guiBoolean) {
            guiIns.update_Spiel_Brett(laufFeld, players, sp + 1)
          } else {
            tuiIns.tui_v1(laufFeld, players)
          }

          // checken ob mind. eine karte moeglich
          //startkarte
          var checkSt = false
          for (kar <- players(sp).getKarten()) {

            if ((kar == 1 || kar == 13 || kar == 14))
              checkSt = true

            //if (players(sp).getStart().size + players(sp).getZiel().size != 4)
            //  checkSt = true

          }
          //if (players(sp).getStart().size == 0)
          //   checkSt = true
          // gui_ausgabe ist fuer hinweis ausgabe bei gui
          var gui_ausgabe = ""
          if (!checkSt) {
            if (!guiBoolean) {
              println("Keine moegliche Startkarte vorhanden.")
            }
            gui_ausgabe = "Keine moegliche Startkarte vorhanden.\n"
          }

          // Blokierte Spielfelder
          var bestePos = getBestPos(laufFeld, sp, players)

          var checkBl = false
          /*Hier wird ausgegeben die kleinste Karte von Spielers Karte und die großte
           Laufmoeglichkeit Abstand von alle Figuren, die im Lauffeld stehen*/
          if (!(players(sp).getAnzKart() == 0) && players(sp).getStart().size != 4) {
            val klKart = players(sp).getkleinsteKarte()
            if (!guiBoolean) {
              println("bestePosforallFig: " + bestePos + " klKart: " + klKart)
            }
            if (bestePos == 0) {
              // keine figur auf dem spielfeld
              checkBl = true
            } else if (bestePos >= klKart)
              //blockiert
              checkBl = true

            if (!checkBl) {
              if (!guiBoolean) {
                println("Keine moegliche Figur vorhanden!(Blockiert)")
              }
              gui_ausgabe += "Keine moegliche Figur vorhanden!(Blockiert)\n"
            }
          }

          // Nur Buben , ohne moeglichkeit
          var km = false
          var allB = true
          for (k <- players(sp).getKarten()) {
            if (k != 11) {
              allB = false
            }
          }

          if (allB && (players(sp).getStart().size + players(sp).getZiel().size == 4)) {
            km = true
          }

          var andere = false
          for (fig <- laufFeld.asInstanceOf[Lauffeld].getFeld()) {
            //wenn figur geschützt ist , nicht tauschen
            var geschuetzt = false
            if (laufFeld.asInstanceOf[Lauffeld].getFeld().map(_.swap).get(players(sp).getStartPos()) != None) {
              geschuetzt = true
            }

            if (!fig._1.getFigur().startsWith(players(sp).getName()) && !geschuetzt)
              andere = true
          }

          if (!andere && allB) {
            if (!guiBoolean) {
              println("Keine moegliche Figur vorhanden!(alle Buben)(keine andere Figur)")
            }
            gui_ausgabe += "Keine moegliche Figur vorhanden!(alle Buben)(keine andere Figur)\n"

          }

          // im ziel blockiert
          var bl = zielBlockiert(sp, players)

          if (bl) {
            if (!guiBoolean) {
              println("Keine moegliche Figur vorhanden!(alle Blockiert)")
            }
            gui_ausgabe += "Keine moegliche Figur vorhanden!(alle Blockiert)\n"
          }
          // evtl andere faelle ?

          // Keine moegliche Zuege erkannt:

          if (!checkSt && !checkBl && ((km || !andere)) && bl) {
            players(sp).delAllKarte()
            if (!guiBoolean) {
              println("Keine Karte zum Spielen: alle Karten wurden abgeworfen")
            }
            gui_ausgabe += "Keine Karte zum Spielen: alle Karten wurden abgeworfen\n"
          }

          /*legit verwendet, um Spieler zu wechseln, wenn sein Schritt(ausfuehren) erfolgreich war*/
          var legit = false
          var gui_prefix_error = ""
          while (!legit) {

            val erg = spielZugErfolgreich(laufFeld, sp, players, spBrett, gui_prefix_error + gui_ausgabe)
            legit = erg._2

            if (!erg._2) {
              gui_prefix_error = "Fehler: Bitte neue Karte angeben.\n"
            }
            if (erg._1 != null) {
              players = erg._1
            }

          }
          // zug ende
          turn = (turn + 1) % 4
        }

        //testen ob runden ende
        var anzKar = 0;
        for (sp <- 1 to players.length) {
          anzKar += players(sp - 1).getAnzKart()
        }

        if (anzKar == 0) {
          // alle spieler haben keine karten mehr - runden ende!
          rundenEnde = true

          if (karGrenz > 2)
            karGrenz -= 1
          else
            karGrenz = 6
        }

      } // while rundenende

      if (players(1).alleImZiel() && players(3).alleImZiel())
        win = 1
      if (players(0).alleImZiel() && players(2).alleImZiel())
        win = 0

    } // while win ende
    var gewinner2 = win + 2
    var gewinner = win + " hat mit seinem Teampartner " + gewinner2 + "gewonnen :)"
    if (guiBoolean) {
      var dial = Dialog.showMessage(null, gewinner, title = "Herzlichen Glueckwuensch!!!")
      if (dial.getClass() == null) {
        sys.exit(0)
      }
    } else {
      println(win + " hat mit seinem Teampartner " + gewinner2 + "gewonnen :)")
    }
  }

  def addSpieler(players: ArrayBuffer[Spieler], anzSp: Int) {
    logger.log(Level.INFO, "addSpieler(): Spieler werden hinzugefuegt")
    for (i <- 1 to anzSp) {
      var spInterface = injector.getInstance(classOf[Spieler])
      var p1 = spInterface.setSpieler(i, (i - 1) * 16);
      players += p1;
    }
  }

  def verteieleKarte(players: ArrayBuffer[Spieler], kartenStapel: Karten, karGrenz: Int) {
    logger.log(Level.INFO, "verteileKarte(): Karten werden verteilt")
    val r = scala.util.Random

    // spieler - default 4

    for (sp <- 1 to players.length) {

      // kartengrenze - default 6
      var kar = ArrayBuffer[Int]()
      for (j <- 1 to karGrenz) {
        val zufall = r.nextInt(kartenStapel.getKarten().length - 1)
        players(sp - 1).setKarte(kartenStapel.getKarten()(zufall))
        kartenStapel.getKarten().remove(zufall)
      }
    }
  }

  def tauscheKarte(players: ArrayBuffer[Spieler], laufFeld: LauffeldInterfaces) {
    logger.log(Level.INFO, "tauscheKarte() Spieler tauschen Karten")
    //init
    var tauschKarten = Array[Int](0, 0, 0, 0)

    //jeder spieler
    for (sp <- 0 to players.length - 1) {

      //Gui ausgeben zum karten anzeigen
      guiIns.update_Spiel_Brett(laufFeld, players, sp + 1)

      var tKart = "0"
      var tk = Benutzerinput(guiBoolean, guiIns).StrToIntK(tKart)
      var check = false
      while (check == false) {

        if (guiBoolean) {
          guiIns.frame_comp.textLabel.text_=(players(sp).getName() + ", Waehle eine Karte zum tauschen aus.")
        } else {
          println(players(sp).getName() + ", Waehle eine Karte zum tauschen aus.")
          println(players(sp).getKartenAusgabe())
        }

        var tKart = ""
        if (!guiBoolean) {
          tKart = scala.io.StdIn.readLine()
        }
        tk = Benutzerinput(guiBoolean, guiIns).StrToIntK(tKart)

        while (tk == None) {

          if (guiBoolean) {
            guiIns.frame_comp.textLabel.text_=(players(sp).getName() + ", Waehle eine Karte zum tauschen aus.")
            if (guiIns.feld_inhalt_kb != "") {
              tKart = guiIns.feld_inhalt_kb
              guiIns.feld_inhalt_kb = ""
            }
          } else {
            println(players(sp).getName() + ", Waehle eine Karte zum tauschen aus.")
            println(players(sp).getKartenAusgabe())
            tKart = scala.io.StdIn.readLine()
          }
          tk = Benutzerinput(guiBoolean, guiIns).StrToIntK(tKart)
        }

        //checken ob karte da ist
        for (k <- players(sp).getKarten()) {

          if (k == tk.get) {
            check = true
          }
        }
        if (!check) {
          if (guiBoolean) {
            guiIns.frame_comp.textLabel.text_=("Diese Karte haben Sie nicht.")
          } else {
            println("Diese Karte haben Sie nicht.")
          }
        }

      }
      tauschKarten((sp + 2) % 4) = tk.get
      players(sp).delKarte(tk.get)
    }

    for (sp <- 1 to players.length) {
      players(sp - 1).setKarte(tauschKarten(sp - 1))
    }
  }

  def getBestPos(laufFeld: LauffeldInterfaces, sp: Int, players: ArrayBuffer[Spieler]): Int = {

    var bestePos = 0

    for (fig <- laufFeld.asInstanceOf[Lauffeld].getFeld()) {
      if (fig._1.getFigur().startsWith(players(sp).getName())) {

        var break = false
        for (i <- 1 to 13) {

          if (op_log(guiBoolean, guiIns).lFIstFrei(laufFeld, players, i + fig._2)) {

            if ((i > bestePos) && !break)
              bestePos = i

          } else {
            break = true

          }
        }
      }
    }
    return bestePos
  }

  def zielBlockiert(sp: Int, players: ArrayBuffer[Spieler]): Boolean = {
    // im ziel blockiert
    var bl = false

    if (!players(sp).getZiel().isEmpty) {

      // Figur nicht im Lauffeld -> Figur ist im Zielfeld

      if (!(players(sp).getAnzKart() == 0)) {
        val klKart = players(sp).getkleinsteKarte()

        for (fig <- players(sp).getZiel()) {
          var pos = fig._2
          var schritt = 4 - pos

          // ------------
          var l = players(sp).getZiel().map(_.swap)

          if (klKart <= schritt) {
            var belegt = false
            for (i <- pos + 1 to 4) {
              if (l.contains(i)) {
                belegt = true
              }
            }
            if (!belegt) {
              bl = true
            }
          }
        }
      }
    }
    return bl
  }

  def spielZugErfolgreich(laufFeld: LauffeldInterfaces, sp: Int, players: ArrayBuffer[Spieler],
    spBrett: Spielbrett, gui_ausgabe: String): (ArrayBuffer[Spieler], Boolean) = {
    logger.log(Level.INFO, "spielZugErfolgreich() gewaehlte Karte wird gespielt")
    // karten anzeigen
    if (!guiBoolean) {
      println("Du bist dran: " + players(sp).getName())
      println("Ass = 1, Bube = 11, Dame = 12, Koenig = 13, Joker = 14, 0 = keine moegliche")
      println(players(sp).getKartenAusgabe())
    }
    val aus1 = "Du bist dran: " + players(sp).getName() + "\n"
    val aus2 = "Ass = 1, Bube = 11, Dame = 12, Koenig = 13, Joker = 14, 0 = keine moegliche\n"
    val aus3 = players(sp).getKartenAusgabe() + "\n"

    if ((players(sp).getAnzKart() == 0)) {
      return (null, true)
    }
    //auswaehlen + loeschen
    val aus4 = "Waehle eine Karte zum Spielen aus."
    if (guiBoolean) {
      guiIns.frame_comp.textLabel.text_=(gui_ausgabe + aus1 + aus4)
    } else {
      println("Waehle eine Karte zum Spielen aus.")
    }
    var spKart = Benutzerinput(guiBoolean, guiIns).karte_waehlen()
    while (spKart == None || (!players(sp).getKarten().contains(spKart.get) && spKart.get != 0)) {

      if (guiBoolean) {
        guiIns.frame_comp.textLabel.text_=(gui_ausgabe + aus1 + aus4)
      } else {
        println("Du hast nicht diese Karte.")
        println("Waehle eine Karte zum Spielen aus.")
      }
      Thread.sleep(500L)
      spKart = Benutzerinput(guiBoolean, guiIns).karte_waehlen()
    }
    // spkart.get ist gecheckt
    var sK = spKart.get

    if (sK == 0) {
      if (guiBoolean) {
        guiIns.frame_comp.textLabel.text_=("Keine Karte zum Spielen.")
      } else {
        println("Keine Karte zum Spielen.")
      }
      // Karten loeschen und naechster spieler
      players(sp).delAllKarte()
      return (null, true)

    } else {

      // kartenlogik ausfuehren

      if (op_log(guiBoolean, guiIns).ausfuehren14(laufFeld, sK, players(sp), players, spBrett)) {
        players(sp).delKarte(sK)
        return (null, true)

      } else if (spBrett.get_revert7()) {
        laufFeld.setFeld(spBrett.get_spiel_Lauf_Feld().getFeld())
        //players-=(players(sp))
        var neuerSp = spBrett.get_spiel_Player()
        var oldPlayers = new ArrayBuffer[Spieler]
        oldPlayers ++= players
        oldPlayers(sp).setZiel(neuerSp.getZiel())
        oldPlayers(sp).setStart(neuerSp.getStart())
        oldPlayers(sp).setKarten(neuerSp.getKarten())
        if (guiBoolean) {
          guiIns.update_Spiel_Brett(laufFeld, oldPlayers, sp + 1)
        } else {
          tuiIns.tui_v1(laufFeld, oldPlayers)
        }
        spBrett.set_revert7(false)
        spBrett.remove_Spiel_Brett()
        if (!guiBoolean) {
          println("Jetzt eine neue Karte angeben.(7)")
        }
        return (oldPlayers, false)
      } else {
        if (!guiBoolean) {
          println("Jetzt eine neue Karte angeben.")
        }
        return (null, false)
      }
    }
  }
}