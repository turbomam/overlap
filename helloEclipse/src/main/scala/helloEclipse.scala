import org.eclipse.rdf4j.model.ValueFactory
import org.eclipse.rdf4j.model.impl.SimpleValueFactory


object helloEclipse {
      def main(args: Array[String]) =
        println("Hello World!")
        
        val factory = SimpleValueFactory.getInstance()
      
}