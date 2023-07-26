# ControlPlane Plugin for IntelliJ IDEA

ControlPlane is a plugin for IntelliJ IDEA that streamlines your workflow by allowing you to checkout GitLab projects directly from a custom toolbox. The plugin interacts with the GitLab API to display a list of all available projects in your GitLab instance. With its integrated search functionality, you can easily find and select a project for checkout.

## Important Note on Directory Replacement During Checkout
Please be aware that during the checkout process, specific directories in opened project will be automatically replaced without any prompts or confirmation messages. This behavior is designed for efficient file handling within the system.
This means any uncommitted changes in these directories may be lost. Before performing a checkout, ensure that all important modifications have been properly saved, committed, or backed up elsewhere.
Remember to always commit or stash your changes to prevent any loss of work.

## Features
- Direct access to your GitLab projects within IntelliJ IDEA.
- Checkout functionality that allows you to clone GitLab projects directly from the custom toolbox.
- Search functionality to find specific projects quickly.
- Configurable GitLab API base URL and GitLab token.

## Installation
ControlPlane can be installed from JetBrains Marketplace:

1. In IntelliJ IDEA, go to `File -> Settings` (or `IntelliJ IDEA -> Preferences` for macOS).
2. Navigate to `Plugins`.
3. Click `Marketplace`.
4. Search for "ControlPlane".
5. Click `Install`.

Alternatively, you can download ControlPlane as a .jar file from the JetBrains Marketplace website and install it manually via `File -> Settings -> Plugins -> Install Plugin from Disk`.

## Configuration
Before you can use ControlPlane, you need to configure your GitLab API base URL and GitLab token:

1. Go to `File -> Settings -> Tools -> ControlPlane`.
2. Enter your GitLab token and GitLab API base URL. The GitLab API base URL is typically `https://<your-gitlab-instance>/api/v4/projects`.
3. Click `OK`.

## Usage
After installation and configuration, a new tool window named "ControlPlane" will appear in IntelliJ IDEA.

To use ControlPlane:

1. Open the "ControlPlane" tool window.
2. If the project list is not already loaded, click `Refresh`.
3. Use the search bar to find a specific project (optional).
4. Click a project from the list. ControlPlane will then checkout the Git repository in your current project's base directory.

## License
ControlPlane is released under the [MIT License](https://opensource.org/licenses/MIT).

## Support
If you encounter any issues or have any questions, please create a new issue on the ControlPlane's GitHub page.

## Contributing
Contributions to ControlPlane are welcome! Please see the CONTRIBUTING.md file for more details.

**Note**: The CONTRIBUTING.md file mentioned in the Contributing section is optional and would need to be created separately if you want to include it. You might also want to consider adding screenshots of ControlPlane in action or specific examples of how to use it to further enhance the README.
