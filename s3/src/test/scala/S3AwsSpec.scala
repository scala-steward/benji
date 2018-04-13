package tests.benji.s3

import akka.stream.Materializer

import play.api.libs.ws.DefaultBodyWritables._

import org.specs2.concurrent.{ ExecutionEnv => EE }
import org.specs2.mutable.Specification

import tests.benji.{ StorageCommonSpec, VersioningCommonSpec }

import com.zengularity.benji.s3.tests.TestUtils

final class S3AwsSpec extends Specification with AwsTests {
  "S3 Amazon" title

  sequential

  awsSuite(
    "in path style",
    TestUtils.aws)(TestUtils.materializer)

  awsSuite(
    "in virtual host style",
    TestUtils.awsVirtualHost)(TestUtils.materializer)

  awsSuite(
    "in virtual host style",
    TestUtils.awsVirtualHostV4)(TestUtils.materializer)

  awsMinimalSuite(
    "in path style with URI",
    TestUtils.awsFromPathStyleURL)(TestUtils.materializer)

  awsMinimalSuite(
    "in virtual host with URI",
    TestUtils.awsFromVirtualHostStyleURL)(TestUtils.materializer)

  awsSuite(
    "in virtual host style with URI V4",
    TestUtils.awsFromVirtualHostStyleURLV4)(TestUtils.materializer)
}

sealed trait AwsTests extends StorageCommonSpec with VersioningCommonSpec with S3Spec { specs: Specification =>
  import TestUtils.withMatEx

  def awsMinimalSuite(
    label: String,
    s3f: => com.zengularity.benji.s3.WSS3)(implicit m: Materializer) = s"S3 client $label" should {
    val bucketName = s"benji-test-${System identityHashCode s3f}"

    withMatEx { implicit ee: EE => minimalCommonTests(s3f, bucketName) }
  }

  def awsSuite(
    label: String,
    s3f: => com.zengularity.benji.s3.WSS3)(implicit m: Materializer) = s"S3 client $label" should {
    val bucketName = s"benji-test-${System identityHashCode s3f}"

    withMatEx { implicit ee: EE =>
      commonTests(s3f, bucketName)
      commonVersioningTests(s3f, sampleVersionId = "t1Uelqn.uwzanWblaNOrIWpgWapViNXY")
    }

    s3Suite(s3f, bucketName, "testfile.txt")
  }
}
