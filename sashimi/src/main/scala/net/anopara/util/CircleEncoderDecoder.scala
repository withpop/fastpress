package net.anopara.util

import java.time.{Instant, LocalDateTime, ZoneId}

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

object CircleEncoderDecoder {
  implicit val TimestampFormat : Encoder[LocalDateTime] with Decoder[LocalDateTime] =
    new Encoder[LocalDateTime] with Decoder[LocalDateTime] {
      override def apply(a: LocalDateTime): Json =
        Encoder.encodeLong.apply(a.atZone(ZoneId.systemDefault()).toInstant.toEpochMilli)
      override def apply(c: HCursor): Result[LocalDateTime] =
        Decoder.decodeLong.map(s => Instant.ofEpochMilli(s)
          .atZone(ZoneId.systemDefault()).toLocalDateTime).apply(c)
    }
}
