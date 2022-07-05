package com.google.idea.gn

import com.google.idea.gn.util.GnCodeInsightTestCase
import org.junit.Test

open class FoldingTest : GnCodeInsightTestCase() {
    @Test
    fun testBlockFolding() {
        var filePath = "$testDataPath/project/src/test/FoldingTest.gn"
        myFixture.testFolding(filePath)
    }
    override fun getTestDataPath() = "src/test/testData"
}
