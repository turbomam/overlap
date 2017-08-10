import org.slf4j.Logger
import org.slf4j.LoggerFactory

object LogTest {
  def main(args: Array[String]) =
    {
      val logger = LoggerFactory.getLogger("unnamed");
      logger.debug("Hello Log");
    }

}
