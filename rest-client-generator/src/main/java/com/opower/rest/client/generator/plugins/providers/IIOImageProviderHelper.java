/**
 *    Copyright 2014 Opower, Inc.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 **/
package com.opower.rest.client.generator.plugins.providers;

import com.opower.rest.client.generator.annotations.ImageWriterParams;
import com.opower.rest.client.generator.util.FindAnnotation;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A IIOImageProviderHelper.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public final class IIOImageProviderHelper
{

   private static final Logger logger = LoggerFactory.getLogger(IIOImageProviderHelper.class);

   private IIOImageProviderHelper()
   {
   }

   /**
    * FIXME Comment this
    *
    * @param mediaType
    * @return
    */
   public static ImageWriter getImageWriterByMediaType(MediaType mediaType)
   {
      Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(mediaType.toString());
      ImageWriter writer = writers.next();
      if (writer == null)
      {
         Response response = Response.serverError().entity("").build();
         throw new WebApplicationException(response);
      }
      return writer;
   }

   /**
    * FIXME Comment this
    *
    * @param in
    * @param mediaType
    * @param imageIndex
    * @return
    * @throws java.io.IOException
    */
   public static IIOImage readImage(InputStream in, ImageReader reader, int imageIndex)
           throws IOException
   {
      ImageInputStream iis = ImageIO.createImageInputStream(in);
      reader.setInput(iis, false);
      return reader.readAll(imageIndex, null);
   }

   /**
    * FIXME Comment this
    *
    * @param mediaType
    * @return
    */
   public static ImageReader getImageReaderByMediaType(MediaType mediaType)
   {
      Iterator<ImageReader> readers = ImageIO.getImageReadersByMIMEType(mediaType.toString());

      ImageReader reader = null;
      while (readers.hasNext())
      {
         reader = (ImageReader) readers.next();
      }

      if (reader == null)
      {
         String[] availableTypes = ImageIO.getReaderMIMETypes();
         logger.warn("A reader for {0} was not found. This provider is currently configured"
                 + "to handle only {1}", mediaType, availableTypes);
         List<Variant> variants = ProviderHelper.getAvailableVariants(availableTypes);
         Response response = Response.notAcceptable(variants).status(Status.NOT_ACCEPTABLE).build();
         throw new WebApplicationException(response);
      }
      return reader;
   }

   /**
    * FIXME Comment this
    *
    * @param annotations
    * @param mediaType
    * @param writer
    * @param out
    * @param image
    * @throws java.io.IOException
    */
   public static void writeImage(Annotation[] annotations,
                                 MediaType mediaType,
                                 ImageWriter writer,
                                 OutputStream out,
                                 IIOImage image)
           throws IOException
   {
      ImageWriteParam param;
      if (mediaType.equals(MediaType.valueOf("image/jpeg")))
      {
         param = new JPEGImageWriteParam(Locale.US);
      }
      else
      {
         param = writer.getDefaultWriteParam();
      }

      /*
      * If the image output type supports compression, set it to the highest
      * maximum
      */
      ImageWriterParams writerParams =
              FindAnnotation.findAnnotation(annotations, ImageWriterParams.class);
      if (writerParams != null)
      {
         if (param.canWriteCompressed())
         {
            param.setCompressionMode(writerParams.compressionMode());
            param.setCompressionQuality(writerParams.compressionQuality());
         }
      }
      else if (param.canWriteCompressed())
      {
         param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
         param.setCompressionQuality(1.0f);
      }
      BufferedOutputStream buff = new BufferedOutputStream(out, 2048);
      ImageOutputStream ios = ImageIO.createImageOutputStream(buff);
      try
      {
         writer.setOutput(ios);
         writer.write(null, image, param);
      }
      finally
      {
         writer.dispose();
      }
   }
}
