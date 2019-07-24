# Friendviewer
Self-hosted remote desktop client & server
--------
## Prototype r1
Prototype revision 1 completed on 7/24/2019. A buggy, minimalist proof-of-concept.
### Build instructions
`bazel build //prototype/distributor/src:server_deploy.jar`

`bazel build //prototype/daemon:daemon_main`

Run the server_deploy.jar on a server of your choice, connect to it with the daemon main application.

Daemon provides the following (working) commands:

`name {name} ; sets nickname`
  
`connect {ip address} ; connects to server, set nickname before this`

`session {target name} ; starts a session with target user`
