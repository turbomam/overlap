import java.util.stream.Collectors

import org.eclipse.rdf4j.model.util.Models
import org.eclipse.rdf4j.model.vocabulary.XMLSchema
import org.eclipse.rdf4j.query.QueryResults
import org.eclipse.rdf4j.repository.http.HTTPRepository

object RDF4JFilterTest {

  def main(args: Array[String]): Unit = {

    // graphdb running on AWS, over HTTP connection
    val rdf4jServer = "http://transformunify.org:7200"
    val repositoryID = "trucks"
    val MyRepo = new HTTPRepository(rdf4jServer, repositoryID)
    MyRepo.initialize()
    var con = MyRepo.getConnection()

    val f = MyRepo.getValueFactory()
    val DumpTruck = f.createIRI("http://example.com/dumpTruck")
    val Weight = f.createIRI("http://example.com/weight")

    val m = QueryResults.asModel(con.getStatements(DumpTruck, Weight, null))
    val intValuesStream = 
      Models.objectLiterals(m).stream()
    val intValuesFiltered = 
      intValuesStream.filter(l => l.getDatatype().equals(XMLSchema.INTEGER))
    val intValues = intValuesFiltered.collect(Collectors.toList())
    
    System.out.println(intValues.toString())

    MyRepo.shutDown

    con.close

  }
}
