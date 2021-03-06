package main.scala.de.htwg.se.DogGame.controller

import main.scala.de.htwg.se.DogGame.model.Spieler
import main.scala.de.htwg.se.DogGame.model.Lauffeld
import main.scala.de.htwg.se.DogGame.view.Tui
import main.scala.de.htwg.se.DogGame.model.Spielbrett
import main.scala.de.htwg.se.DogGame.model.Spielfigur
import main.scala.de.htwg.se.DogGame.view.SwingGui
import scala.util.control.Breaks._
import org.apache.logging.log4j.{ LogManager, Logger, Level }
import main.scala.de.htwg.se.DogGame.model.LauffeldInterfaces
import main.scala.de.htwg.se.DogGame.model.SpielerInterfaces
import main.scala.de.htwg.se.DogGame.model.LauffeldInterfaces
import main.scala.de.htwg.se.DogGame.model.LauffeldInterfaces
import main.scala.de.htwg.se.DogGame.model.LauffeldInterfaces

case class Operationlogik(guiBoolean: Boolean, guiIns: SwingGui) {

  import main.scala.de.htwg.se.DogGame.controller.Benutzerinput
  import scala.collection.mutable.ArrayBuffer
  
  import com.google.inject.Guice
  import main.scala.de.htwg.se.DogGame.DependencyModule
  val injector = Guice.createInjector(new DependencyModule(guiBoolean))
  //var figurInterface = injector.getInstance(classOf[Spielfigur])
  var lFInterface = injector.getInstance(classOf[Lauffeld])
  //var spInterface = injector.getInstance(classOf[Spieler])
  
  //var figurInterface = new Spielfigur()
  //var lFInterface = new Lauffeld()
  //var spInterface = new Spieler()

  val logger: Logger = LogManager.getLogger(this.getClass.getName)

  def ausfuehren14(lF: LauffeldInterfaces, karte: Int, spieler: Spieler, alleSp: ArrayBuffer[Spieler], spBrett: Spielbrett): Boolean = {
    logger.log(Level.INFO, "ausfuehren14(): ueberprueft, ob Karte Jocker ist")
    var karteE = karte
    if (karte == 14) {
      var opt = Benutzerinput(guiBoolean, guiIns).waehle_Joker()
      karteE = opt
    }
    return ausfuehren(lF, karteE, spieler, alleSp, spBrett)
  }

  def ausfuehren(lF: LauffeldInterfaces, karte: Int, spieler: Spieler, alleSp: ArrayBuffer[Spieler], spBrett: Spielbrett): Boolean = {

    logger.log(Level.INFO, "ausfuehren(): Karten werden abgefangen")
    var tmp = spieler.getName() + Benutzerinput(guiBoolean, guiIns).figur_waehlen()
    var fig = spieler.getFig(tmp)

    karte match {
      case 0 => { // keine moegliche karte Etwas lief falsch

        if (guiBoolean) {
          guiIns.frame_comp.textLabel.text_=("Es gibt keine ausspielbare Karte.")
        } else {
          println("Es gibt keine ausspielbare Karte.")
        }
        return true
      }
      case 1 => { // ASS

        // optionen bestimmen
        var opt = -1
        do {
          if (!guiBoolean) {
            println("0 -> Aus Startfeld rausgehen.")
            println("1 -> 1 Schritt im Lauffeld weiter laufen.")
            println("11 -> 11 Schritte im Lauffeld weiter laufen.")
          }
          val ausg = "0 -> Aus Startfeld rausgehen.\n" +
            "1 -> 1 Schritt im Lauffeld weiter laufen.\n" +
            "11 -> 11 Schritte im Lauffeld weiter laufen.\n"

          opt = Benutzerinput(guiBoolean, guiIns).opt_waehlen(ausg)
        } while (opt != 0 && opt != 11 && opt != 1)

        if (opt == 0) {
          return laufen0(lF, fig, spieler, alleSp)
        } else {
          return laufenN(lF, fig, opt, spieler, alleSp)
        }

      }

      case 4 => {

        // optionen bestimmen
        var opt = -1
        do {
          if (!guiBoolean) {
            println("14 -> 4 Schritte im Lauffeld Rueckwaerts laufen.")
            println("4 -> 4 Schritte im Lauffeld weiter laufen.")
          }
          val ausg = "14 -> 4 Schritte im Lauffeld Rueckwaerts laufen.\n" +
            "4 -> 4 Schritte im Lauffeld weiter laufen.\n"

          opt = Benutzerinput(guiBoolean, guiIns).opt_waehlen(ausg)
        } while (opt != 14 && opt != 4)

        if (opt == 14) {
          return laufen14(lF, fig, spieler, alleSp)
        } else {
          return laufenN(lF, fig, opt, spieler, alleSp)
        }

      }
      case 7 => {

        var parlF = lFInterface
        parlF.setFeld(lF.asInstanceOf[Lauffeld].getFeld())
        var alteSp = injector.getInstance(classOf[SpielerInterfaces])
        alteSp.setZiel(spieler.getZiel())
        alteSp.setStart(spieler.getStart())
        alteSp.setKarten(spieler.getKarten())
        spBrett.set_spiel_Lauf_Feld(parlF)
        spBrett.set_spiel_Player(alteSp)

        for (i <- 1 to 7) {
          // optionen bestimmen
          if (!laufen7(lF, fig, spieler, alleSp, spBrett)) {
            spBrett.set_revert7(true)
            spieler.setZiel(alteSp.asInstanceOf[Spieler].getZiel())
            return false
          }

          if (i != 7) {
            var tmp = spieler.getName() + Benutzerinput(guiBoolean, guiIns).figur_waehlen()
            fig = spieler.getFig(tmp)
          }
        }
        return true
      }

      case 11 => { // Bube

        return laufen15(lF, fig, spieler, alleSp)
      }

      case 13 => { // Koenig

        // optionen bestimmen
        var opt = -1
        do {
          if (!guiBoolean) {
            println("0 -> Aus Startfeld rausgehen.")
            println("13 -> 13 Schritte im Lauffeld weiter laufen.")
          }
          val ausg = "0 -> Aus Startfeld rausgehen.\n" +
            "13 -> 13 Schritte im Lauffeld weiter laufen.\n"
          opt = Benutzerinput(guiBoolean, guiIns).opt_waehlen(ausg)
        } while (opt != 0 && opt != 13)

        if (opt == 0) {
          return laufen0(lF, fig, spieler, alleSp)
        } else {
          return laufenN(lF, fig, opt, spieler, alleSp)
        }

      }
      case _ => {
        // der rest - normale karten: 2,3,5,6,8,9,10,12
        return laufenN(lF, fig, karte, spieler, alleSp)

      }
    }
  }

  def schickStart(lF: LauffeldInterfaces, alleSp: ArrayBuffer[Spieler], pos: Int) = {
    logger.log(Level.INFO, "schickStart(): Figur wird zu Startfeld geschickt.")
    // get figur
    
    var fig = new Spielfigur().setSpielfigur("0")
    for ((k, v) <- lF.asInstanceOf[Lauffeld].getFeld()) {
      if (v == pos) {
        fig = k
      }
    }
    // loesche im lF
    lF.asInstanceOf[Lauffeld].getFeld() -= ((fig))

    // setze in zielSpieler.start
    for (sp <- alleSp) {
      if (fig.getFigur().startsWith(sp.getName())) {
        sp.getStart() += ((fig, Integer.valueOf(fig.getFigur().substring(1))))

      }
    }
  }

  def lFIstFrei(lF: LauffeldInterfaces, p: ArrayBuffer[Spieler], pos: Int): Boolean = {
    logger.log(Level.INFO, "lFIstFrei(): Ueberprueft ob Lauffeld frei ist wir zu Startfeld geschiekct.")
    var l = lF.asInstanceOf[Lauffeld].getFeld().clone().map(_.swap)
    for (einer <- p) {
      //auf einer startposition
      if (pos == einer.getStartPos()) {
        if (l.get(pos) != None) {
          //die richtige figur auf dieser startPos
          if (l.get(pos).get.getFigur().startsWith(einer.getName())) {
            if (!guiBoolean) {
              println("Block erkannt.")
            }
            return false
          }
        }
      }
    }

    return true
  }

  def laufen0(lF: LauffeldInterfaces, figur: Spielfigur, spieler: Spieler, alleSp: ArrayBuffer[Spieler]): Boolean = {
    logger.log(Level.INFO, "laufen0(): Aus Startfeld rausgehen.")
    if (!spieler.delFigur(figur)) {
      if (guiBoolean) {
        guiIns.frame_comp.textLabel.text_=("Etwas lief falsch - keine Figur geloescht")
      } else {
        println("Etwas lief falsch - keine Figur geloescht")
      }
      return false
    }
    if (lF.posBelegt(spieler.getStartPos())) {
      //nachhause schicken
      schickStart(lF, alleSp, spieler.getStartPos())
    }

    //aus dem start gehen
    lF.asInstanceOf[Lauffeld].getFeld() += ((figur, spieler.getStartPos()))
    return true

  }

  def laufen7(lF: LauffeldInterfaces, figur: Spielfigur, spieler: Spieler, alleSp: ArrayBuffer[Spieler], spBrett: Spielbrett): Boolean = {
    // 7
    logger.log(Level.INFO, "laufen7(): Option 7.")
    if (lF.asInstanceOf[Lauffeld].getFeld().contains(figur)) {
      var pos = lF.asInstanceOf[Lauffeld].getFeld().get(figur)
      var erg = (pos.get + 1) % 64

      if (!lFIstFrei(lF, alleSp, erg)) {
        if (guiBoolean) {
          guiIns.frame_comp.textLabel.text_=("Blockiert durch StartFigur")
        } else {
          println("Blockiert durch StartFigur")
        }
        return false
      }

      lF.asInstanceOf[Lauffeld].getFeld().remove(figur)

      var vorPos = 1000

      if (spBrett.get_spiel_Lauf_Feld().getFeld().get(figur) != None) {
        vorPos = spBrett.get_spiel_Lauf_Feld().getFeld().get(figur).get % 16
        //println("Zugriff erfolgt!")
      }

      var bisZiel = 64 - spieler.getStartPos()
      if (pos.get == spieler.getStartPos() && (9 <= vorPos && vorPos <= 15)) {
        // Ins Ziel laufen wenn davor vor dem ziel war( 9 bis 15)!
        val zielPos = erg - spieler.getStartPos()
        spieler.getZiel() += ((figur, zielPos))
      } else {
        // noch eine runde laufen
        if (lF.posBelegt(erg))
          //figur schlagen
          schickStart(lF, alleSp, erg)
        lF.asInstanceOf[Lauffeld].getFeld() += ((figur, erg))
      }
    } else if (spieler.getZiel().contains(figur)) {
      // Figur nicht im Lauffeld -> Figur ist im Zielfeld
      var pos = spieler.getZiel().get(figur)
      var schritt = 4 - pos.get
      var l = spieler.getZiel().map(_.swap)

      if (1 <= schritt) {
        var belegt = false
        if (l.contains(pos.get + 1)) {
          belegt = true
        }
        if (!belegt) {
          spieler.getZiel() -= figur
          spieler.getZiel() += ((figur, pos.get + 1))
        } else {
          if (guiBoolean) {
            guiIns.frame_comp.textLabel.text_=("Figur kann sich nicht mehr laufen!")
          } else {
            println("Figur kann sich nicht mehr laufen!")
          }
          return false
        }
      } else {

        return false
      }
    } else {
      // Figur nicht im Lauffeld , Figur ist nicht im Zielfeld -> Figur im Startfeld
      return false
    }
    // tui anzeigen
    if (guiBoolean) {
      guiIns.update_Spiel_Brett(lF, alleSp, spieler.getId())
    } else {
      Tui().tui_v1(lF, alleSp)
    }

    return true
  }

  def laufen14(lF: LauffeldInterfaces, figur: Spielfigur, spieler: Spieler, alleSp: ArrayBuffer[Spieler]): Boolean = {
    // 4 zurueck
    logger.log(Level.INFO, "laufen14(): Option 4 ruecklauf.")
    if (lF.asInstanceOf[Lauffeld].getFeld().contains(figur)) {
      var pos = lF.asInstanceOf[Lauffeld].getFeld().get(figur)
      lF.asInstanceOf[Lauffeld].getFeld().remove(figur)
      var erg = (pos.get - 4)

      if (erg < 0) {
        erg = 64 + erg
      }

      erg = erg % 64

      if (lF.posBelegt(erg))
        //figur schlagen
        schickStart(lF, alleSp, erg)
      lF.asInstanceOf[Lauffeld].getFeld() += ((figur, erg))
      return true
    } else {
      //Figur nicht im Lauffeld
      if (guiBoolean) {
        guiIns.frame_comp.textLabel.text_=("Figur nicht im Lauffeld: bitte nochmal eingeben")
      } else {
        println("Figur nicht im Lauffeld: bitte nochmal eingeben");
      }
      return false
    }

  }

  def laufen15(lF: LauffeldInterfaces, figur: Spielfigur, spieler: Spieler, alleSp: ArrayBuffer[Spieler]): Boolean = {
    logger.log(Level.INFO, "laufen15(): Bube, Figur tauschen.")
    var checkAndere = false
    for (figs <- lF.asInstanceOf[Lauffeld].getFeld().seq) {
      if (!figs._1.getFigur().startsWith(spieler.getName()))
        checkAndere = true
    }

    if (!checkAndere) {
      return false
    }
    /*Tauschen mit Figuren, die sich im Lauffeld befinden.*/
    if (guiBoolean) {
      guiIns.frame_comp.textLabel.text_=("Figur von andere Spieler waehlen!")
    } else {
      println("Figur von andere Spieler waehlen! (z.B. R1)");
    }

    var fig_name = "" //R1
    var fig2 = new Spielfigur().setSpielfigur("Z")
    var gui_prefix = ""
    // Check falls tauschbar
    do {
      if (guiBoolean) {
        if (fig_name == "") {
          guiIns.frame_comp.textLabel.text_=("Figur von anderem Spieler waehlen!")
        } else {
          guiIns.frame_comp.textLabel.text_=(gui_prefix + "Figur von anderem Spieler waehlen!")
        }
        Thread.sleep(500L)
        fig_name = Benutzerinput(guiBoolean, guiIns).spFigur_waehlen_gui()
      } else {
        println("Diese Figur kann man nicht tauschen.")
        println("Figur von anderem Spieler waehlen! (z.B. R1)");
        fig_name = Benutzerinput(guiBoolean, guiIns).spFigur_waehlen()
      }
      fig2 = lF.getFigur(fig_name)
      gui_prefix = "Diese Figur kann man nicht tauschen.\n"
    } while ((fig2 == null) || (!lF.asInstanceOf[Lauffeld].getFeld().contains(fig2) && fig2.getFigur().startsWith(spieler.getName())))

    if (lF.asInstanceOf[Lauffeld].getFeld().get(figur) == None || lF.asInstanceOf[Lauffeld].getFeld().get(fig2) == None) {
      if (guiBoolean) {
        guiIns.frame_comp.textLabel.text_=("Entweder falsche Figur gewählt oder falsche Name eingegeben!")
      } else {
        println("Entweder falsche Figur gewählt oder falsche Name eingegeben!")
      }
      return false
    }
    // get erledigt
    var pos = lF.asInstanceOf[Lauffeld].getFeld().get(figur).get
    var pos2 = lF.asInstanceOf[Lauffeld].getFeld().get(fig2).get

    lF.asInstanceOf[Lauffeld].getFeld().remove(figur)
    lF.asInstanceOf[Lauffeld].getFeld().remove(fig2)

    lF.asInstanceOf[Lauffeld].getFeld() += ((figur, pos2))
    lF.asInstanceOf[Lauffeld].getFeld() += ((fig2, pos))
    return true
  }

  def laufenN(lF: LauffeldInterfaces, figur: Spielfigur, opt: Int, spieler: Spieler, alleSp: ArrayBuffer[Spieler]): Boolean = {
    logger.log(Level.INFO, "laufenN(): Normal laufen.")
    // im normalfall : 1-13
    if (lF.asInstanceOf[Lauffeld].getFeld().contains(figur)) {
      var pos = lF.asInstanceOf[Lauffeld].getFeld().get(figur)
      var erg = (pos.get + opt) % 64

      //was wenn blockiert
      for (i <- pos.get + 1 to pos.get + opt) {
        var s = i % 64
        if (!lFIstFrei(lF, alleSp, s)) {
          if (guiBoolean) {
            guiIns.frame_comp.textLabel.text_=("Diese Figur ist blockiert.")
          } else {
            println("Diese Figur ist blockiert.")
          }
          return false
        }
      }

      lF.asInstanceOf[Lauffeld].getFeld().remove(figur)

      if ((pos.get < spieler.getStartPos() || (spieler.getId() == 1 && pos.get > 4)) && 1 <= (erg - spieler.getStartPos()) % 64 && (erg - spieler.getStartPos()) % 64 <= 4) {
        // Ins Ziel laufen

        val zielPos = erg - spieler.getStartPos()

        var posZielF = 4
        for (f <- spieler.getZiel()) {
          if (posZielF > f._2) {
            posZielF = f._2
          }
        }

        if (posZielF != 4) {
          posZielF -= 1
        }

        if (posZielF != 0 && zielPos <= posZielF) {
          spieler.getZiel() += ((figur, zielPos))
        } else {
          if (guiBoolean) {
            guiIns.frame_comp.textLabel.text_=("Zielfeld nicht erreichbar!")
          } else {
            println("Zielfeld nicht erreichbar!")
          }
          return false
        }
      } else {
        // noch eine runde laufen
        if (lF.posBelegt(erg))
          //figur schlagen
          schickStart(lF, alleSp, erg)
        lF.asInstanceOf[Lauffeld].getFeld() += ((figur, erg))
      }
    } else if (spieler.getZiel().contains(figur)) {
      // Figur nicht im Lauffeld -> Figur ist im Zielfeld

      var pos = spieler.getZiel().get(figur)

      if (pos == None) {
        if (guiBoolean) {
          guiIns.frame_comp.textLabel.text_=("Figur nicht gefunden.")
        } else {
          println("Figur nicht gefunden.")
        }
        return false
      }
      var schritt = 4 - pos.get
      var l = spieler.getZiel().map(_.swap)

      if (opt <= schritt) {
        var belegt = false
        for (i <- pos.get + 1 to schritt) {
          if (l.contains(i)) {
            belegt = true
          }
        }
        if (!belegt) {
          spieler.getZiel().remove(figur)
          spieler.getZiel() += ((figur, pos.get + schritt))
        } else {
          if (guiBoolean) {
            guiIns.frame_comp.textLabel.text_=("Figur kann sich nicht mehr laufen!")
          } else {
            println("Figur kann sich nicht mehr laufen!")
          }
          return false
        }

      }
    } else {
      // Figur nicht im Lauffeld , Figur ist nicht im Zielfeld -> Figur im Startfeld
      if (guiBoolean) {
        guiIns.frame_comp.textLabel.text_=("Diese Figur ist im Startfeld.")
      } else {
        println("Diese Figur ist im Startfeld.")
      }
      return false
    }
    return true

  }
}