package main.scala.de.htwg.se.DogGame.view

import scala.swing._
//used to listen
import scala.swing.event._
// for borderlayout
import scala.swing.BorderPanel.Position._
//used for dice
import scala.util.Random
//Use the ImageIcon directly from Java
import javax.swing.ImageIcon
import javax.swing.UIManager
import javax.swing.border.Border
import java.awt
import scala.collection.mutable.ArrayBuffer
import main.scala.de.htwg.se.DogGame.model.Spieler
import main.scala.de.htwg.se.DogGame.model.Spielfigur
import _root_.main.scala.de.htwg.se.DogGame.model.Lauffeld
import _root_.main.scala.de.htwg.se.DogGame.model.LauffeldInterfaces

//object SwingGui extends SimpleSwingApplication {
class SwingGui extends MainFrame with SwingGuiInterface {

  import _root_.main.scala.de.htwg.se.DogGame.view.FrameComp

  title = "DogGame"
  //contents = ui
  var frame_comp = new FrameComp()
  contents = frame_comp.ui

  // variable zum turn merken
  var aktueller_turn = 1

  // werte fuer button clicked
  var feld_inhalt_lfb = ""
  var feld_inhalt_kb = ""
  var feld_inhalt_tab = ""
  var tausch_figur = ""

  var lf_buttons = frame_comp.lf_buttons //collection.mutable.Map[Int, Button]()
  var sp_st_zi_fl = frame_comp.sp_st_zi_fl //collection.mutable.Map[String, (ArrayBuffer[Button], ArrayBuffer[Button])]()
  var sp_kar_but = frame_comp.sp_karten
  var but_farbe = java.awt.Color.white
  var spieler = Array("B", "R", "G", "S")
  var kar_buttons = new ArrayBuffer[Button]()
  var spieler_farben = Array(
    java.awt.Color.blue,
    java.awt.Color.red, java.awt.Color.green,
    java.awt.Color.yellow)

  for (sp <- spieler) {
    var arr = sp_kar_but.get(sp)
    for (b <- arr.get) {
      kar_buttons.append(b)
    }
  }

  for (pos <- 0 to 63) {
    listenTo(lf_buttons.get(pos).get)
  }

  for (sp <- spieler) {
    var st_feld = sp_st_zi_fl.get(sp).get._1
    var zi_feld = sp_st_zi_fl.get(sp).get._2
    for (pos <- 0 to 3) {
      listenTo(st_feld(pos))
      listenTo(zi_feld(pos))
    }
  }
  listenTo(frame_comp.buttonOk)

  listenTo(frame_comp.buttonB1)
  listenTo(frame_comp.buttonB2)
  listenTo(frame_comp.buttonB3)
  listenTo(frame_comp.buttonB4)
  listenTo(frame_comp.buttonB5)
  listenTo(frame_comp.buttonB6)

  listenTo(frame_comp.buttonR1)
  listenTo(frame_comp.buttonR2)
  listenTo(frame_comp.buttonR3)
  listenTo(frame_comp.buttonR4)
  listenTo(frame_comp.buttonR5)
  listenTo(frame_comp.buttonR6)

  listenTo(frame_comp.buttonG1)
  listenTo(frame_comp.buttonG2)
  listenTo(frame_comp.buttonG3)
  listenTo(frame_comp.buttonG4)
  listenTo(frame_comp.buttonG5)
  listenTo(frame_comp.buttonG6)

  listenTo(frame_comp.buttonS1)
  listenTo(frame_comp.buttonS2)
  listenTo(frame_comp.buttonS3)
  listenTo(frame_comp.buttonS4)
  listenTo(frame_comp.buttonS5)
  listenTo(frame_comp.buttonS6)

  var butID = 0
  var clickAnz = 0
  reactions += {
    case e: ButtonClicked => {
      if (e.source.text == "Ok") {
        var result = frame_comp.nutzerEingabe.text
        set_inhalt_kb("")
        set_inhalt_lfb("", java.awt.Color.white)
        set_tausch_lfb("", java.awt.Color.white)
        set_inhalt_tab(result)
        frame_comp.nutzerEingabe.text_=("")
      } else if (e.source.text != "*") {
        // e.source.text ist die zahl
        if (e.source.background == spieler_farben(aktueller_turn - 1)) {
          if (!kar_buttons.contains(e.source)) {
            set_inhalt_kb("")
            set_tausch_lfb("", java.awt.Color.white)
            set_inhalt_tab("")
            set_inhalt_lfb(e.source.text, e.source.background)
          } else {
            set_tausch_lfb("", java.awt.Color.white)
            set_inhalt_tab("")
            set_inhalt_lfb("", e.source.background)
            set_inhalt_kb(e.source.text)
          }
        } else {
          if (!kar_buttons.contains(e.source)) {
            set_inhalt_tab("")
            set_inhalt_lfb("", e.source.background)
            set_inhalt_kb("")
            set_tausch_lfb(e.source.text, e.source.background)
          }
        }
      }
    }
  }

  override def update_Spiel_Brett(lF: LauffeldInterfaces, p: ArrayBuffer[Spieler], turn: Int) {
    aktueller_turn = turn

    var l = lF.asInstanceOf[Lauffeld].getFeld().map(_.swap)

    /*Um alte Farbe und text zu loeschen von Lauffeld*/
    for (pos <- 0 to 63) {
      var button = lf_buttons.get(pos).get
      button.text_=("*")
      button.background_=(java.awt.Color.white)
    }

    for (pos <- 0 to 63) {
      if (l.contains(pos)) {
        var fig_name = l.get(pos).get.getFigur()
        var button = lf_buttons.get(pos).get
        button.text_=(fig_name(1).toString())
        if (fig_name.startsWith("B")) {
          button.background_=(java.awt.Color.blue)
        } else if (fig_name.startsWith("R")) {
          button.background_=(java.awt.Color.red)
        } else if (fig_name.startsWith("G")) {
          button.background_=(java.awt.Color.green)
        } else if (fig_name.startsWith("S")) {
          button.background_=(java.awt.Color.yellow)
        }
      }
    }
    /*Um alte Farbe und text zu loeschen von Start und Zielfeld*/

    for (sp <- spieler) {
      var st_feld = sp_st_zi_fl.get(sp).get._1
      var zi_feld = sp_st_zi_fl.get(sp).get._2
      for (pos <- 0 to 3) {
        var button_st = st_feld(pos)
        button_st.text_=("*")
        button_st.background_=(java.awt.Color.white)
        var button_zi = zi_feld(pos)
        button_zi.text_=("*")
        button_zi.background_=(java.awt.Color.white)
      }

    }

    for (sp <- p) {
      var sp_name = sp.getName()
      var sp_st_feld = sp.getStart().map(_.swap)
      var sp_zi_feld = sp.getZiel().map(_.swap)
      var sp_karten = sp.getKarten()
      var st_feld = sp_st_zi_fl.get(sp_name).get._1
      var zi_feld = sp_st_zi_fl.get(sp_name).get._2
      var kar_buttons = sp_kar_but.get(sp_name).get
      if (sp_name == "B") {
        for (pos <- 1 to 4) {
          if (sp_st_feld.contains(pos)) {
            var button = st_feld(pos - 1)
            var but_text = sp_st_feld.get(pos).get.getFigur().charAt(1).toString()
            button.text_=((pos).toString())
            button.background_=(java.awt.Color.blue)
          }
          if (sp_zi_feld.contains(pos)) {
            var button = zi_feld(pos - 1)
            var but_text = sp_zi_feld.get(pos).get.getFigur().charAt(1).toString()
            button.text_=(but_text)
            button.background_=(java.awt.Color.blue)
          }
        }
        for (k <- 0 to 5) {
          kar_buttons(k).background_=(java.awt.Color.blue)
          if (k <= sp_karten.length - 1) {
            kar_buttons(k).text_=(sp_karten(k).toString())
          } else {
            kar_buttons(k).text_=("")
          }
        }
      } else if (sp_name == "R") {
        for (pos <- 1 to 4) {
          if (sp_st_feld.contains(pos)) {
            var button = st_feld(pos - 1)
            var but_text = sp_st_feld.get(pos).get.getFigur().charAt(1).toString()
            button.text_=(but_text)
            button.background_=(java.awt.Color.red)
          }
          if (sp_zi_feld.contains(pos)) {
            var button = zi_feld(pos - 1)
            var but_text = sp_zi_feld.get(pos).get.getFigur().charAt(1).toString()
            button.text_=(but_text)
            button.background_=(java.awt.Color.red)
          }
        }
        for (k <- 0 to 5) {
          kar_buttons(k).background_=(java.awt.Color.red)
          if (k <= sp_karten.length - 1) {
            kar_buttons(k).text_=(sp_karten(k).toString())
          } else {
            kar_buttons(k).text_=("")
          }
        }
      } else if (sp_name == "G") {
        for (pos <- 1 to 4) {
          if (sp_st_feld.contains(pos)) {
            var button = st_feld(pos - 1)
            var but_text = sp_st_feld.get(pos).get.getFigur().charAt(1).toString()
            button.text_=(but_text)
            button.background_=(java.awt.Color.green)
          }
          if (sp_zi_feld.contains(pos)) {
            var button = zi_feld(pos - 1)
            var but_text = sp_zi_feld.get(pos).get.getFigur().charAt(1).toString()
            button.text_=(but_text)
            button.background_=(java.awt.Color.green)
          }
        }
        for (k <- 0 to 5) {
          kar_buttons(k).background_=(java.awt.Color.green)
          if (k <= sp_karten.length - 1) {
            kar_buttons(k).text_=(sp_karten(k).toString())
          } else {
            kar_buttons(k).text_=("")
          }
        }
      } else {
        for (pos <- 1 to 4) {
          if (sp_st_feld.contains(pos)) {
            var button = st_feld(pos - 1)
            var but_text = sp_st_feld.get(pos).get.getFigur().charAt(1).toString()
            button.text_=(but_text)
            button.background_=(java.awt.Color.yellow)
          }
          if (sp_zi_feld.contains(pos)) {
            var button = zi_feld(pos - 1)
            var but_text = sp_zi_feld.get(pos).get.getFigur().charAt(1).toString()
            button.text_=(but_text)
            button.background_=(java.awt.Color.yellow)
          }
        }
        for (k <- 0 to 5) {
          kar_buttons(k).background_=(java.awt.Color.yellow)
          if (k <= sp_karten.length - 1) {
            kar_buttons(k).text_=(sp_karten(k).toString())
          } else {
            kar_buttons(k).text_=("")
          }
        }
      }
      if (turn == sp.getId()) {
        kar_buttons(6).text_=("<-")
        kar_buttons(6).background_=(java.awt.Color.lightGray)
      } else {
        kar_buttons(6).text_=("")
        kar_buttons(6).background_=(frame_comp.buttonOkPanel.background)
      }
    }
  }

  def set_inhalt_tab(inh: String) {
    feld_inhalt_tab = inh
  }

  def set_inhalt_kb(inh: String) {
    feld_inhalt_kb = inh
  }

  def set_inhalt_lfb(inh: String, farbe: java.awt.Color) {
    feld_inhalt_lfb = inh
    but_farbe = farbe
  }

  def set_tausch_lfb(inh: String, farbe: java.awt.Color) {
    tausch_figur = inh
    but_farbe = farbe
  }
  
  def gratualtion_fenster(text: String){
    var textHinweis = new swing.TextArea()
    textHinweis.lineWrap_=(true)
    textHinweis.wordWrap_=(true)
    textHinweis.editable_=(false)
    textHinweis.background_=(java.awt.Color.green)
    textHinweis.text_=(text)
    contents = textHinweis
  }
}

