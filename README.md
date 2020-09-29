# Salesforce ALM Framework - CI/CD Pipelines for DX

Groovy pipelines for Jenkins to perform ALM (Application Lifecycle Management) operations over Salesforce platform using metadata DX.

These pipelines are meant to be used altogether the Pyhon scripts from [ALM-SF-DX-Python-Tools](https://github.com/Accenture/ALM-SF-DX-Python-Tools).

Currently supporting:
- Gitlab CE
- Gitlab EE
- Gitlab.com
- Bitbucket Cloud 
- Bitbucket Server

## Git2SF

Automatic pipeline triggered from a Merge / Pull Request creation or modification. The job performs a validation / deployment of the differences exisiting between the source and target branches in the configured org. 
Detailed explanation and job configuration can be found at [Git2SF README](/Git2SF_README.md)

## DeployerOpenMR

Handles the manualy executed jobs to validate/deploy metadata into Salesforce using as input parameters the Merge / Pull Request ID with the differences to be deployed and the target org/sandbox.

Detailed explanation and job configuration can be found at [DeployerOpenMR README](deployerOpenMR_README.md)

## Deployer

Handles the manualy executed jobs to validate/deploy metadata into Salesforce using as input parameters the source and target commits hash / tags names and the target org/sandbox.

Detailed explanation and job configuration can be found at [Deployer README](Deployer_README.md)


# Contributors Guide

## Contribution

We encourage you to contribute to new features or fixes. You can start by opening a
[Github issue](https://github.com/Accenture/ALM-SF-DX-Pipelines/issues) to get feedback from other contributors.

## License

The Salesforce ALM Framework is licensed under the Apache License 2.0 - see [LICENSE](LICENSE) for details.
