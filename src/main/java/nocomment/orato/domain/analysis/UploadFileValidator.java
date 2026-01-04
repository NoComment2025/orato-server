package nocomment.orato.domain.analysis;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class UploadFileValidator {

    private UploadFileValidator() {
    }

    public static boolean isSupportedSound(MultipartFile file) throws IOException {
        String extension = extension(file.getOriginalFilename());
        byte[] header = readHeader(file);
        return switch (extension) {
            case "wav" -> matches(header, 0, "RIFF") && matches(header, 8, "WAVE");
            case "mp3" -> matches(header, 0, "ID3") || hasMpegFrameSync(header);
            case "flac" -> matches(header, 0, "fLaC");
            case "ogg", "oga", "opus" -> matches(header, 0, "OggS");
            case "aac" -> hasAdtsSync(header);
            case "m4a" -> matches(header, 4, "ftyp");
            default -> false;
        };
    }

    public static boolean isSupportedVideo(MultipartFile file) throws IOException {
        String extension = extension(file.getOriginalFilename());
        byte[] header = readHeader(file);
        return switch (extension) {
            case "mp4", "mov", "3gp" -> matches(header, 4, "ftyp");
            case "webm", "mkv" -> startsWith(header, 0x1A, 0x45, 0xDF, 0xA3);
            case "avi" -> matches(header, 0, "RIFF") && matches(header, 8, "AVI ");
            default -> false;
        };
    }

    private static String extension(String filename) {
        if (filename == null) {
            return "";
        }
        int dot = filename.lastIndexOf('.');
        return dot < 0 ? "" : filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static byte[] readHeader(MultipartFile file) throws IOException {
        try (InputStream input = file.getInputStream()) {
            return input.readNBytes(12);
        }
    }

    private static boolean matches(byte[] header, int offset, String signature) {
        byte[] expected = signature.getBytes(StandardCharsets.US_ASCII);
        if (header.length < offset + expected.length) {
            return false;
        }
        for (int i = 0; i < expected.length; i++) {
            if (header[offset + i] != expected[i]) {
                return false;
            }
        }
        return true;
    }

    private static boolean startsWith(byte[] header, int... signature) {
        if (header.length < signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            if ((header[i] & 0xFF) != signature[i]) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasMpegFrameSync(byte[] header) {
        return header.length >= 2 && (header[0] & 0xFF) == 0xFF
                && (header[1] & 0xE0) == 0xE0
                && (header[1] & 0x18) != 0x08
                && (header[1] & 0x06) != 0;
    }

    private static boolean hasAdtsSync(byte[] header) {
        return header.length >= 2 && (header[0] & 0xFF) == 0xFF && (header[1] & 0xF6) == 0xF0;
    }
}
