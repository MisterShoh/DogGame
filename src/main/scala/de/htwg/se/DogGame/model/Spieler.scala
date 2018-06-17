package main.scala.de.htwg.se.DogGame.model

class Spieler(id: Int, st: Int) extends SpielerInterfaces{
 
  import scala.collection.mutable.ArrayBuffer
  import main.scala.de.htwg.se.DogGame.model.Spielfigur
  import main.scala.de.htwg.se.DogGame.model.Karten
  
  private var spielerId = id;
  private var startPos = st
  
  private var karten = new Karten() 
  private var start = collection.mutable.Map[Spielfigur, Int]();
  private var ziel = collection.mutable.Map[Spielfigur, Int]();
  private var figuren = ArrayBuffer[Spielfigur]();
  
  

  override def getId(): Int = {
    return spielerId;
  }

  override def getName(): String = {
    spielerId match {
      case 1 => return "B"
      case 2 => return "R"
      case 3 => return "G"
      case 4 => return "S"

    }
  }

  override def setStart() {
    for (i <- 1 to 4) {
      spielerId match {
        case 1 => {
          var fig1 = new Spielfigur("B" + i) 
          start += ((fig1, i))
          figuren += fig1  
        }
        case 2 => {
          var fig2 = new Spielfigur("R" + i)
          start += ((fig2, i))
          figuren += fig2
          }
        case 3 => {
          var fig3 = new Spielfigur("G" + i)
          start += ((fig3, i))
          figuren += fig3
        }
        case 4 => {
          var fig4 = new Spielfigur("S" + i)
          start += ((fig4, i))
          figuren += fig4
        }
      }
    }
  }

  override def getStart(): collection.mutable.Map[Spielfigur, Int] = {
    return start
  }

  override def getZiel(): collection.mutable.Map[Spielfigur, Int] = {
    return ziel
  }
  
  override def setKarte(kar: Int) {
    karten.setKarte(kar)
  }
  
  override def getAnzKart(): Int = {
    return karten.getAnzKart()
  }

  override def getKarten(): ArrayBuffer[Int] = {
    return karten.getKarten();
  }
  
  override def getkleinsteKarte(): Int = {
    if(karten.getKarten().contains(14))
      return 1

    if (karten.getKarten().min == 11) {
      var ret = ArrayBuffer[Int]()
      for (k <- karten.getKarten().clone()) {
        if (k != 11)
          ret += k
      }
      if(!ret.isEmpty)
        return ret.min 
    } 
      
    return karten.getKarten().min
  }

  override def getKartenAusgabe(): String = {
    var hand = "Deine Handkarten: "

    for (i <- karten.getKarten()) {
      hand += i.toString() + ", "
    }

    return hand;
  }

  override def delKarte(kar: Int) {
    karten.getKarten() -= kar
  }
  
  override def delAllKarte() {
    karten.getKarten().clear()
  }

  override def delFigur(fig: Spielfigur): Boolean = {

    var check = false
    
    if (start.contains(fig))
      
      check = true
      start -= (fig)

    return check

  }

  override def getStartPos(): Int = {
    return startPos
  }
  
  override def alleImZiel(): Boolean = {
    
    if(ziel.size == 4)
      return true
    
    return false
  }
  
  override def getFig(fig: String):Spielfigur = {
  
    for(figur <- figuren){
      if(figur.getFigur().startsWith(fig))
        return figur
    }
    return null
  }
}