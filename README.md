# README

## autumo ifaceX - Additional Office Interface Processor Readers & Writers

This is a maven eclipse project and serves consist of additional office autumo
ifaceX Interface Processor (IP) Readers and Writers and is extending the core 
reader and writer library of the autumo ifaceX platform.

If you want to install the project properly, you need the Maven console
tools installed on your computer (e.g. command 'mvn') and a local Maven
repository.

Of course, you can also just look at the source files and use what you need
in your own project and IDE.

### Install: Plug into ifaceX

1.	Export the ZIP archive.

2.	Copy all libraries from the lib/ folder to the ifaceX-lib/ folder.

3.	Comment out the corresponding readers and writers in the ifaceX
	configuration 'cfg/interfaces.cfg'; see section 'Office Writers'.
	This can be done in the ifaceX Studio too; see Menu 'Configuration' 
	-> 'Interfaces Configuration'.

### Build

1.	You can Import the project in eclipse or in any other development IDE.

2.	Copy the following libraries from the ifaceX Installation directory'
	into the 'lib/'-directory of this project:
	
	- lib/autumo-commons-x.y.z.jar
	- lib/autumo-ifacex-x.y.z.jar
	- lib/autumo-beetroot-x.y.z.jar
	
3.	Execute 'maven-install.sh|bat' so above libraries are installed into 
	your local Maven repository; only available with autumo ifaceX, see
	https://products.autumo.ch.
	
4.	Execute 'make.sh' to create a package that can be deployed into an
	ifaceX installation directory. 

### Additional Office Readers | Writers

- Excel Writer (XLSX)
