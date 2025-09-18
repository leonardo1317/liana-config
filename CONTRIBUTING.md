# Contributing to Liana

Thank you for your interest in contributing to **Liana**!  
We welcome contributions of all sizes, from bug reports and documentation improvements to new features and optimizations. By contributing, you agree to follow our guidelines and help us maintain a high-quality, consistent codebase.

---

## How to Contribute

1. **Fork the repository** to your own GitHub account.  
2. **Clone your fork** locally:

   ```bash
   git clone https://github.com/<your-username>/liana-config.git
   cd liana-config
   ```

3. **Create a branch** for your changes:

   ```bash
   git checkout -b feature/my-feature
   ```

4. **Make your changes**, following the guidelines below.  
5. **Run tests** to ensure your changes do not break existing functionality.  
6. **Commit your changes** with a descriptive message:

   ```bash
   git commit -m "Add feature X"
   ```

7. **Push your branch** to your fork:

   ```bash
   git push origin feature/my-feature
   ```

8. **Open a Pull Request** against the `main` branch of this repository.

---

## Reporting Bugs

- Check existing issues to see if your bug has already been reported.  
- Create a **new issue** with a clear title and description, including:  
  - Steps to reproduce  
  - Expected vs. actual behavior  
  - Environment details (OS, Java version, Liana version)  
  - Minimal reproducible example if possible

---

## Submitting a Feature

- Discuss major features first in a GitHub issue before implementing.  
- Ensure your feature aligns with **Liana's philosophy**: simplicity, adaptability, and minimalism.  
- Include **unit tests** and **documentation updates** when relevant.

---

## Code Guidelines

- Java 17+ is required.  
- Follow the [official Java code conventions](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html).  
- Make code **thread-safe**, **immutable when possible**, and maintain **clarity** over cleverness.  
- Include **JavaDocs** for public APIs.  
- Avoid introducing dependencies unless absolutely necessary.

---

## Testing

- All new features or bug fixes must include **unit tests**.  
- Run tests with:

  ```bash
  ./gradlew test
  ```

- Ensure tests pass on local machine before submitting a PR.  
- Prefer **functional-style, Optional usage**, and **null-safe code patterns**.

---

## Style and Formatting

- Keep code **clean, readable, and maintainable**.  
- Use **meaningful names** for variables, methods, and classes.  
- Avoid unnecessary nesting and complexity.  
- Follow **Liana's logging conventions** for debugging or verbose output.

---

## Pull Request Process

1. Fork → Branch → Commit → Push → PR.  
2. PR should reference relevant issues (if any).  
3. Include **descriptive title** and **detailed description**.  
4. Ensure CI builds successfully and all tests pass.  
5. Reviewers may request **changes or improvements**; respond constructively.

---

## License

By contributing, you agree that your contributions will be licensed under the **Apache License 2.0**, the same as the project.

---

## Thank You!

Your contributions help make **Liana** more robust, flexible, and enjoyable for the entire Java community.  
Together, we build a configuration library that truly **adapts to developers**.

---

> "Configuration that adapts to you, not the other way around."

