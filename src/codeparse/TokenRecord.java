package codeparse;

import java.util.concurrent.atomic.AtomicReference;

public record TokenRecord(boolean isToken, AtomicReference<String> content) {
}
