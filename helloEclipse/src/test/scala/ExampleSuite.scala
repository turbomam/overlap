

import org.scalatest.FunSuite
 
class ExampleSuite extends FunSuite {
 
  test("test 2 expected values are equal") {
    assert(2 === 2)
  }
 
  test("test 2 values are different and fail") {
    assert(2 === 3)
  }
}