import org.eclipse.rdf4j.query.QueryLanguage
import org.eclipse.rdf4j.repository.http.HTTPRepository
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager._

object sharedMain {
  def main(args: Array[String]): Unit = {

    // // sparql connection method not expected to support RDF4J CRUD methods on connection objects
    //    val sparqlEndpoint = "https://dbpedia.org/sparql"
    //    val MyRepo = new SPARQLRepository(sparqlEndpoint)

    //    // RDF4J Server running on AWS
    //    val rdf4jServer = "http://transformunify.org:8080/rdf4j-server/"
    //    val repositoryID = "example-db"

    // graphdb running on AWS, over HTTP connection
    val rdf4jServer = "http://transformunify.org:7200"
    val repositoryID = "turbo"

    //    // stardog running on AWS, over HTTP connection
    //    val rdf4jServer = "http://transformunify.org:5820"
    //    val repositoryID = "mark_march"

    /*
					 * 10:50:45.912 [main] DEBUG o.e.r.rio.LanguageHandlerRegistry - Registered service class org.eclipse.rdf4j.rio.languages.BCP47LanguageHandler
Exception in thread "main" org.eclipse.rdf4j.repository.http.HTTPQueryEvaluationException: could not read protocol version from server: 
	at org.eclipse.rdf4j.repository.http.HTTPTupleQuery.evaluate(HTTPTupleQuery.java:56)
	at sharedMain$.main(sharedMain.scala:68)
	at sharedMain.main(sharedMain.scala)
Caused by: org.eclipse.rdf4j.repository.RepositoryException: could not read protocol version from server: 
	at org.eclipse.rdf4j.repository.http.HTTPRepository.useCompatibleMode(HTTPRepository.java:366)
	at org.eclipse.rdf4j.repository.http.HTTPRepositoryConnection.flushTransactionState(HTTPRepositoryConnection.java:629)
	at org.eclipse.rdf4j.repository.http.HTTPTupleQuery.evaluate(HTTPTupleQuery.java:48)
	... 2 more
Caused by: java.lang.NumberFormatException: For input string: "false"
	at java.lang.NumberFormatException.forInputString(Unknown Source)
	at java.lang.Integer.parseInt(Unknown Source)
	at java.lang.Integer.parseInt(Unknown Source)
	at org.eclipse.rdf4j.repository.http.HTTPRepository.useCompatibleMode(HTTPRepository.java:363)
	... 4 more
					 */

    val MyRepo = new HTTPRepository(rdf4jServer, repositoryID)
    MyRepo.initialize()
    var con = MyRepo.getConnection()

    // graphdb running on aws...  I ahve heard that the repository manager rally only works for RDF4J (and GraphDB?)
    //    val RemoteServerAddr = "http://transformunify.org:7200"
    //    val RemoteRepoName = "turbo"

    //    // stardog running on aws
    //    val RemoteServerAddr = "http://transformunify.org:5820"
    //    val RemoteRepoName = "mark_march"

    /*
					 * 0:28:18.106 [main] DEBUG o.e.r.rio.LanguageHandlerRegistry - Registered service class org.eclipse.rdf4j.rio.languages.BCP47LanguageHandler
Exception in thread "main" org.eclipse.rdf4j.repository.RepositoryException: could not read protocol version from server: 
	at org.eclipse.rdf4j.repository.http.HTTPRepository.useCompatibleMode(HTTPRepository.java:366)
	at org.eclipse.rdf4j.repository.http.HTTPRepositoryConnection.flushTransactionState(HTTPRepositoryConnection.java:629)
	at org.eclipse.rdf4j.repository.http.HTTPRepositoryConnection.exportStatements(HTTPRepositoryConnection.java:282)
	at org.eclipse.rdf4j.repository.http.HTTPRepositoryConnection.getStatements(HTTPRepositoryConnection.java:269)
	at org.eclipse.rdf4j.repository.config.RepositoryConfigUtil.getIDStatement(RepositoryConfigUtil.java:239)
	at org.eclipse.rdf4j.repository.config.RepositoryConfigUtil.hasRepositoryConfig(RepositoryConfigUtil.java:79)
	at org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager.createRepository(RemoteRepositoryManager.java:216)
	at org.eclipse.rdf4j.repository.manager.RepositoryManager.getRepository(RepositoryManager.java:377)
	at sharedMain$.main(sharedMain.scala:31)
	at sharedMain.main(sharedMain.scala)
Caused by: java.lang.NumberFormatException: For input string: "false"
	at java.lang.NumberFormatException.forInputString(Unknown Source)
	at java.lang.Integer.parseInt(Unknown Source)
	at java.lang.Integer.parseInt(Unknown Source)
	at org.eclipse.rdf4j.repository.http.HTTPRepository.useCompatibleMode(HTTPRepository.java:363)
	... 9 more
					 */

    //    val RepoMan = new RemoteRepositoryManager(RemoteServerAddr)
    //    RepoMan.initialize()
    //    val MyRepo = RepoMan.getRepository(RemoteRepoName)
    //    var con = MyRepo.getConnection()

    val f = MyRepo.getValueFactory()

    // Crud: create, RDF4J add solution (many variants)
    val am = f.createIRI("http://example.org/amsterdam")
    val labelProp = f.createIRI("http://www.w3.org/2000/01/rdf-schema#label")
    con.add(am, labelProp, f.createLiteral("Amsterdam"))
    con.add(am, labelProp, f.createLiteral("Berlin"))

    // cRud: read (sparql select solution.  also use rdf4j operations. )
    // also do construct, describe.  
    // also "update" (insert replacements + delete originals?)
    var queryString = """SELECT * 
      WHERE { values ?s { <http://example.org/amsterdam> } .
      ?s rdfs:label ?o } 
      limit 3 """
    val tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString)
    var tqResult = tupleQuery.evaluate()
    while (tqResult.hasNext()) { // iterate over the result
      val bindingSet = tqResult.next()
      val valueOfS = bindingSet.getValue("s")
      val valueOfO = bindingSet.getValue("o")
      val toPrint = valueOfS + " -> " + valueOfO
      println(toPrint)
    }

    tqResult.close()

    // cRud: read, rdf4j getStatements solution, analogous to sparql select
    //    val labelEnt = f.createIRI("http://www.w3.org/2000/01/rdf-schema#label")
    var gsResult = con.getStatements(am, labelProp, null, false)
    while (gsResult.hasNext()) {
      val st = gsResult.next()

      val firstValue = st.getSubject
      val secondValue = st.getObject

      val toPrint = firstValue + " -> " + secondValue

      println(toPrint)
    }

    gsResult.close()

    // cRud: read, rdf4j hasStatement solution, analogous to sparql ask
    var hsResult = con.hasStatement(am, labelProp, null, false)
    var toPrint = "\n" + hsResult.toString()
    println(toPrint)

    //    hsResult.close()

    // cRud: read, sparql construct... rdf4j equivalent?
    queryString = """construct {<http://example.org/amsterdam> <http://example.com/lookAtMe> ?l } 
      where { <http://example.org/amsterdam> <http://www.w3.org/2000/01/rdf-schema#label> ?l } """

    var getQuery = con.prepareGraphQuery(QueryLanguage.SPARQL, queryString)
    var gqResult = getQuery.evaluate()

    //    println("\n")
    //    println(gqResult.toString())
    //    println("\n")

    while (gqResult.hasNext()) {
      val st = gqResult.next()

      val theSub = st.getSubject
      val thePred = st.getPredicate
      val theObj = st.getObject

      val toPrint = theSub + " " + thePred + " " + theObj

      println(toPrint)

    }

    println("\n")

    // cRud: read, sparql describe... rdf4j equivalent?
    queryString = """describe  <http://www.w3.org/2000/01/rdf-schema#label> """

    getQuery = con.prepareGraphQuery(QueryLanguage.SPARQL, queryString)
    gqResult = getQuery.evaluate()

    //    println("\n")
    //    println(gqResult.toString())
    //    println("\n")

    while (gqResult.hasNext()) {
      val st = gqResult.next()

      val theSub = st.getSubject
      val thePred = st.getPredicate
      val theObj = st.getObject

      val toPrint = theSub + " " + thePred + " " + theObj

      println(toPrint)

    }

    println("\n")

    //    gqResult.close()

    // cruD delete, rdf4j solution
    con.remove(am, labelProp, f.createLiteral("Berlin"))

    var getRes = con.getStatements(am, labelProp, null, false)
    while (getRes.hasNext()) {
      val st = getRes.next()

      val firstValue = st.getSubject
      val secondValue = st.getObject

      val toPrint = firstValue + " -> " + secondValue

      println(toPrint)
    }

    MyRepo.shutDown

    con.close

    //    RepoMan.shutDown()

    println("\ndone")

  }
}
