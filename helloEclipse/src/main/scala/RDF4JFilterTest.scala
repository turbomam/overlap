import org.eclipse.rdf4j.query.QueryLanguage
import org.eclipse.rdf4j.repository.http.HTTPRepository
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager._

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.eclipse.rdf4j.query.QueryResults
import org.eclipse.rdf4j.query.QueryResults._

import scala.collection.JavaConversions._

import org.eclipse.rdf4j.model
import org.eclipse.rdf4j.model._
import org.eclipse.rdf4j.model.Statement

import org.eclipse.rdf4j.model
import org.eclipse.rdf4j.model._
import org.eclipse.rdf4j.model.Literal

import org.eclipse.rdf4j.model.vocabulary.XMLSchema

import org.eclipse.rdf4j.model.util.Models

import java.util.stream
import java.util.stream._
import java.util.stream.Collector

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

    // cRud: read, rdf4j getStatements solution, analogous to sparql select
    var gsResult = con.getStatements(DumpTruck, Weight, null)
    
    val m = QueryResults.asModel(con.getStatements(DumpTruck, Weight, null))
    val intValuesStream = 
      Models.objectLiterals(m).stream()
    val intValuesFiltered = 
      intValuesStream.filter(l => l.getDatatype().equals(XMLSchema.INTEGER))
    val intValues = intValuesFiltered.collect(Collectors.toList())
    
    System.out.println(intValues.toString())

    gsResult.close()

    MyRepo.shutDown

    con.close

    //    RepoMan.shutDown()

    println("\ndone")

  }
}
