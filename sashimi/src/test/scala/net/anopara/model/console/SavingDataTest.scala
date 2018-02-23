package net.anopara.model.console

import org.scalatest.FunSuite

class SavingDataTest extends FunSuite {
  test("json parse test") {
    val json =
      """
        |{
        |"title":"title",
        |"category":9,
        |"pathName":"link",
        |"tags":[7],
        |"newTags":["aaa"],
        |"createdAt": 1519071725225,
        |"status":"draft",
        |"attribute":"attr",
        |"body":"body\nbody"}
      """.stripMargin

    val data = SavingData.parseFrom(json)

    assert(data.isDefined)
    assert(data.get.title === "title")
    assert(data.get.category === 9)
    assert(data.get.pathName === "link")
    assert(data.get.tags.head === 7)
    assert(data.get.postedAt.getYear === 2018)
    assert(data.get.status === "draft")
    assert(data.get.attribute === "attr")
    assert(data.get.body === "body\nbody")

  }
}
