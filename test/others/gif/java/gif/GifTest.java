package gif;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import zserio.runtime.ZserioError;
import zserio.runtime.io.SerializeUtil;
import gif.gif_data.*;
import gif.rgb_color.RgbColor;
import gif.screen_descriptor.ScreenDescriptor;

public class GifTest
{
    @Test
    public void onePixGif() throws ZserioError, IOException
    {
        final File file = new File(ONE_PIX_GIF_FILE_NAME);
        final GifFile gifFile = SerializeUtil.deserializeFromFile(GifFile.class, file);

        final short[] signatureFormat = gifFile.getSignature().getFormat();
        final String fileFormat = String.format("%1$c%2$c%3$c", signatureFormat[0], signatureFormat[1],
                signatureFormat[2]);
        assertEquals(GIF_FILE_FORMAT, fileFormat);

        final short[] signatureVersion = gifFile.getSignature().getVersion();
        final String fileVersion = String.format("%1$c%2$c%3$c", signatureVersion[0], signatureVersion[1],
                signatureVersion[2]);
        assertEquals(GIF_FILE_VERSION, fileVersion);

        final ScreenDescriptor screenDescriptor = gifFile.getScreen();
        assertEquals(GIF_SCREEN_WIDTH, screenDescriptor.getWidth());
        assertEquals(GIF_SCREEN_HEIGHT, screenDescriptor.getHeight());
        assertEquals(GIF_SCREEN_BG_COLOR, screenDescriptor.getBgColor());
        assertEquals(GIF_SCREEN_BITS_OF_COLOR_RESOLUTION, screenDescriptor.getBitsOfColorResolution());
        assertEquals(GIF_SCREEN_BITS_PER_PIXEL, screenDescriptor.getBitsPerPixel());
    }

    @Test
    public void dumpGif() throws Exception
    {
        alignment = "";
        final File file = new File(ONE_PIX_GIF_FILE_NAME);
        final GifFile gifFile = SerializeUtil.deserializeFromFile(GifFile.class, file);

        final short[] signature = gifFile.getSignature().getFormat();
        System.out.format(alignment + "Header: %1$c%2$c%3$c", signature[0], signature[1], signature[2]);
        final short[] version = gifFile.getSignature().getVersion();
        System.out.format(alignment + ", %1$c%2$c%3$c%n", version[0], version[1], version[2]);

        System.out.format(alignment + "Size: %1$d x %2$d%n", gifFile.getScreen().getWidth(),
                gifFile.getScreen().getHeight());
        System.out.format(alignment + "Back color: %1$d%n", gifFile.getScreen().getBgColor());
        System.out.format(alignment + "Color res.: %1$d%n", gifFile.getScreen().getBitsOfColorResolution());
        System.out.format(alignment + "Bits per pixel: %1$d%n", gifFile.getScreen().getBitsPerPixel());
        if (gifFile.getScreen().getGlobalColorMapFollows() == 1)
        {
            System.out.print(alignment + "global ");
            printColorMap(gifFile.getScreen().getGlobalColorMap());
        }
        else
        {
            System.out.println(alignment + "No global color map found.");
        }

        GifData gifData = gifFile.getBlocks();
        BlockType gifTag;
        do
        {
            gifTag = gifData.getTag();
            switch(gifTag)
            {
                case EXTENSION_BLOCK:
                    printExtensionBlock(gifData.getBlock().getExtension());
                    break;

                case IMAGE_BLOCK:
                    printImageBlock(gifData.getBlock().getImages());
                    break;

                case TERMINATOR_BLOCK:
                    System.out.println(alignment + "End of file reached.");
                    break;

                default:
                    System.out.println(alignment + "unknown block.");
                    break;
            }
            if (gifTag != BlockType.TERMINATOR_BLOCK)
                gifData = gifData.getNextBlock();

        } while (gifTag != BlockType.TERMINATOR_BLOCK);

        assertTrue(true);
    }

    private void printColorMap(RgbColor[] globalColorMap)
    {
        System.out.println(alignment + "ColorMap:");
        int entry = 0;
        increaseAlignment();
        for (int rowcnt = globalColorMap.length / 16; rowcnt > 0; rowcnt--)
        {
            System.out.print(alignment);
            for (int i = 0; i < 16; i++, entry++)
            {
                final String sep = (i == 16-1) ? "%n" : ", ";
                final RgbColor rgbColor = globalColorMap[entry];
                System.out.format("#%1$02X%2$02X%3$02X" + sep,
                        rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue());
            }
        }
        System.out.print(alignment);
        for (int i = globalColorMap.length % 16; i > 0; i--, entry++)
        {
            final String sep = (i == 1) ? "%n" : ", ";
            final RgbColor rgbColor = globalColorMap[entry];
            System.out.format("#%1$02X%2$02X%3$02X" + sep,
                    rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue());
        }
        decreaseAlignment();
        System.out.println();
    }

    private void printComment(SubBlock block, short byteCount)
    {
        final short[] commentData = block.getDataBytes();
        for (int i = 0; i < byteCount; i++)
            System.out.print((char)commentData[i]);

        byteCount = block.getBlockTerminator();
        if (byteCount > 0)
            printComment(block.getNextData(), byteCount);
    }

    private int calcBlockSize(SubBlock blockData)
    {
        int byteCount = blockData.getBlockTerminator();
        if (byteCount == 0)
            return 0;
        return calcBlockSize(blockData.getNextData()) + byteCount;
    }

    private int calcBlockSize(ZippedSubBlock blockData)
    {
        int byteCount = blockData.getBlockTerminator();
        if (byteCount == 0)
            return 0;
        return calcBlockSize(blockData.getNextData()) + byteCount;
    }

    private void printExtensionBlock(ExtensionBlock extensionBlock) throws IOException
    {
        switch(extensionBlock.getExtensionFunctionCode())
        {
            case PLAINTEXT_EXTENSION:
            {
                System.out.println(alignment + "Plain text:");
                increaseAlignment();
                PlainTextExtension plainText = extensionBlock.getExtension().getPlainTextData();
                final short byteCount = plainText.getByteCount();
                if (byteCount > 0)
                    printComment(plainText.getPlainTextData(), byteCount);
                decreaseAlignment();
                break;
            }

            case GRAPHICCONTROL_EXTENSION:
            {
                System.out.println(alignment + "Graphic control extension:");
                break;
            }

            case COMMENT_EXTENSION:
            {
                System.out.print(alignment + "Comment: ");
                final CommentExtension comment = extensionBlock.getExtension().getCommentData();
                final short byteCount = comment.getByteCount();
                if (byteCount > 0)
                    printComment(comment.getCommentData(), byteCount);
                System.out.println();
                break;
            }

            case APPLICATIONEXTENSION:
            {
                System.out.println(alignment + "Application extension:");
                increaseAlignment();
                final ApplicationExtension appData = extensionBlock.getExtension().getApplicationData();

                System.out.print(alignment + "Appl-ID: ");
                final short[] appID = appData.getApplicationIdentifier();
                for (int i = 0; i < 8; i++)
                    System.out.print((char)appID[i]);
                System.out.println();

                final short[] applCode = appData.getAuthenticationCode();
                System.out.format(alignment + "Appl-Code: %1$c%2$c%3$c%n", applCode[0], applCode[1],
                        applCode[2]);

                int applDataSize = 0;
                if (appData.getApplDataSize() > 0)
                    applDataSize = appData.getApplDataSize() + calcBlockSize(appData.getApplicationData());
                System.out.format(alignment + "Appl-Data size: %1$d Bytes%n", applDataSize);
                decreaseAlignment();
                break;
            }

            default:
            {
                System.out.println(alignment + "unknown extension.");
                break;
            }
        }
        System.out.println();
    }

    private void printImageDescriptor(ImageDescriptor imgDesc)
    {
        System.out.println(alignment + "Image descriptor:");
        increaseAlignment();
        System.out.format(alignment + "Layer size: %1$d x %2$d%n", imgDesc.getWidth(), imgDesc.getHeight());
        System.out.format(alignment + "Layer pos: %1$d, %2$d%n", imgDesc.getTop(), imgDesc.getLeft());
        System.out.format(alignment + "Layer interlaced: %1$s%n",
                (imgDesc.getInterlacedFormatted() == 1) ? "yes" : "no");
        System.out.format(alignment + "Layer bits per pixel: %1$d%n", imgDesc.getBitsPerPixel());
        if (imgDesc.getLocalColorMapFollows() == 1)
        {
            System.out.print(alignment + "local ");
            printColorMap(imgDesc.getLocalColorMap());
        }
        else
        {
            System.out.println(alignment + "No local color map found.");
        }
        decreaseAlignment();
    }

    private void printRasterData(RasterData rasterData)
    {
        int rasterDataSize = rasterData.getCodeSize();
        if (rasterDataSize > 0)
        {
            if (rasterData.getData().getByteCount() > 0)
                rasterDataSize += rasterData.getData().getByteCount() +
                calcBlockSize(rasterData.getData().getDataBytes());
        }
        System.out.format(alignment + "Raster Data size: %1$d Bytes%n", rasterDataSize);
    }

    private void printImageBlock(ImageBlock imageBlock)
    {
        System.out.println(alignment + "Image block:");
        increaseAlignment();
        printImageDescriptor(imageBlock.getImage());
        printRasterData(imageBlock.getData());
        decreaseAlignment();

        System.out.println();
    }

    private void increaseAlignment()
    {
        alignment += "    ";
    }

    private void decreaseAlignment()
    {
        alignment = alignment.substring(0, alignment.length() - 4);
    }

    private String alignment;

    private static final String ONE_PIX_GIF_FILE_NAME = "data" + File.separator + "1pix.gif";

    private static final String GIF_FILE_FORMAT = "GIF";
    private static final String GIF_FILE_VERSION = "89a";
    private static final int    GIF_SCREEN_WIDTH = 256;
    private static final int    GIF_SCREEN_HEIGHT = 256;
    private static final short  GIF_SCREEN_BG_COLOR = 255;
    private static final byte   GIF_SCREEN_BITS_OF_COLOR_RESOLUTION = 7;
    private static final byte   GIF_SCREEN_BITS_PER_PIXEL = 7;
}
